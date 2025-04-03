import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableCaption,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

type SignUpResponseType = {
  username: string;
  salt: string;
  normalHash: string;
  saltHash: string;
  pepperHash: string;
  saltPepperHash: string;
  success?: boolean;
  error?: string;
};

type LoginResponseType = {
  salt: boolean;
  normalHash: boolean;
  saltHash: boolean;
  pepperHash: boolean;
  saltPepperHash: boolean;
  success?: boolean;
  error?: string;
};

const App = () => {
  const [signupUsername, setSignupUsername] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [loginUsername, setLoginUsername] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [signupResponse, setSignupResponse] = useState<SignUpResponseType | null>(null);
  const [loginResponse, setLoginResponse] = useState<LoginResponseType | null>(null);
  const [signupError, setSignupError] = useState<string | null>(null);
  const [loginError, setLoginError] = useState<string | null>(null);

  const handleSignup = async () => {
    setSignupResponse(null);
    setSignupError(null);
    try {
      const response = await fetch("http://localhost:3000/sign-up", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: signupUsername, password: signupPassword }),
      });

      const data: SignUpResponseType = await response.json();

      if (!response.ok || !data.success) {
        setSignupError(data.error || `Signup failed with status: ${response.status}`);
      } else {
        setSignupResponse(data);
      }
    } catch (error: any) {
      setSignupError(error.message || "An unexpected error occurred during signup.");
    }
  };

  const handleLogin = async () => {
    setLoginResponse(null);
    setLoginError(null);
    try {
      const response = await fetch("http://localhost:3000/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: loginUsername, password: loginPassword }),
      });

      const data: LoginResponseType = await response.json();

      if (!response.ok || !data.success) {
        setLoginError(data.error || `Login failed with status: ${response.status}`);
      } else {
        setLoginResponse(data);
      }
    } catch (error: any) {
      setLoginError(error.message || "An unexpected error occurred during login.");
    }
  };

  return (
    <div className="flex flex-col justify-center items-center min-h-screen bg-gray-100 space-y-8 p-4">
      <div className="flex space-x-4">
        {/* Signup Card */}
        <Card className="w-96">
          <CardHeader>
            <CardTitle className="text-2xl font-semibold">Sign Up</CardTitle>
            <CardDescription>Create a new account</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="signup-username">Username</Label>
              <Input
                id="signup-username"
                placeholder="Enter your username"
                value={signupUsername}
                onChange={(e) => setSignupUsername(e.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="signup-password">Password</Label>
              <Input
                id="signup-password"
                type="password"
                placeholder="Enter your password"
                value={signupPassword}
                onChange={(e) => setSignupPassword(e.target.value)}
              />
            </div>
            {signupError && <p className="text-red-500 text-sm">{signupError}</p>}
            {signupResponse?.success && (
              <p className="text-green-500 text-sm">Account created successfully!</p>
            )}
          </CardContent>
          <CardFooter>
            <Button className="w-full" onClick={handleSignup}>
              Create Account
            </Button>
          </CardFooter>
        </Card>

        {/* Login Card */}
        <Card className="w-96">
          <CardHeader>
            <CardTitle className="text-2xl font-semibold">Login</CardTitle>
            <CardDescription>Sign in to your account</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="login-username">Username</Label>
              <Input
                id="login-username"
                placeholder="Enter your username"
                value={loginUsername}
                onChange={(e) => setLoginUsername(e.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="login-password">Password</Label>
              <Input
                id="login-password"
                type="password"
                placeholder="Enter your password"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
              />
            </div>
            {loginError && <p className="text-red-500 text-sm">{loginError}</p>}
            {loginResponse?.success && (
              <p className="text-green-500 text-sm">Logged in successfully!</p>
            )}
          </CardContent>
          <CardFooter>
            <Button className="w-full" onClick={handleLogin}>
              Log In
            </Button>
          </CardFooter>
        </Card>
      </div>

      {/* Response Display */}
      {(signupResponse || loginResponse) && (
        <div className="mt-8 w-full max-w-2xl">
          <h2 className="text-xl font-semibold mb-4">Server Response</h2>
          {signupResponse && signupResponse.success && (
            <div className="mb-4">
              <h3 className="text-lg font-semibold">Sign Up Response:</h3>
              <div className="rounded-md border">
                <Table>
                  <TableCaption>Details of the signup response from the server.</TableCaption>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-[150px]">Field</TableHead>
                      <TableHead>Value</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    <TableRow>
                      <TableCell className="font-medium">username</TableCell>
                      <TableCell>{signupResponse.username}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">salt</TableCell>
                      <TableCell>{signupResponse.salt}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">normalHash</TableCell>
                      <TableCell>{signupResponse.normalHash}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">saltHash</TableCell>
                      <TableCell>{signupResponse.saltHash}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">pepperHash</TableCell>
                      <TableCell>{signupResponse.pepperHash}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">saltPepperHash</TableCell>
                      <TableCell>{signupResponse.saltPepperHash}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </div>
            </div>
          )}
          {signupResponse && signupResponse.error && (
            <p className="text-red-500 text-sm mb-4">Sign Up Error: {signupResponse.error}</p>
          )}

          {loginResponse && (
            <div>
              <h3 className="text-lg font-semibold">Login Response:</h3>
              <div className="rounded-md border">
                <Table>
                  <TableCaption>Result of the login attempt.</TableCaption>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-[150px]">Field</TableHead>
                      <TableHead>Status</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    <TableRow>
                      <TableCell className="font-medium">normalHash</TableCell>
                      <TableCell>{loginResponse.normalHash ? "true" : "false"}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">saltHash</TableCell>
                      <TableCell>{loginResponse.saltHash ? "true" : "false"}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">pepperHash</TableCell>
                      <TableCell>{loginResponse.pepperHash ? "true" : "false"}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">saltPepperHash</TableCell>
                      <TableCell>{loginResponse.saltPepperHash ? "true" : "false"}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </div>
            </div>
          )}
          {loginResponse && loginResponse.error && (
            <p className="text-red-500 text-sm">Login Error: {loginResponse.error}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default App;
