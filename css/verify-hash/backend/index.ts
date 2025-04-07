import crypto from 'crypto';
import express, { Express, Request, Response } from 'express';
import { Pool, Client } from 'pg';

// --- Configuration ---
const app: Express = express();
const port = process.env.PORT || 3000;
const secretKey = process.env.SECRET_KEY || 'my-secret-key';

// --- Middleware ---
const applyCorsHeaders = (_: Request, res: Response, next: () => void) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  next();
};

app.use(applyCorsHeaders);
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// --- Database Configuration ---
interface DbConfig {
  host?: string;
  database?: string;
  user?: string;
  password?: string;
}

const dbConfig: DbConfig = {
  host: process.env.POSTGRES_HOST || 'db', // since using docker networks, else localhost
  database: process.env.POSTGRES_DB || 'database',
  user: process.env.POSTGRES_USER || 'username',
  password: process.env.POSTGRES_PASSWORD || 'password',
};

// --- Database Pool ---
const pool = new Pool(dbConfig);

// --- Database Initialization ---
async function initializeDatabase() {
  const client = await pool.connect();
  try {
    await client.query(`
      CREATE TABLE IF NOT EXISTS users(
        username VARCHAR(255) PRIMARY KEY,
        salt VARCHAR(255) NOT NULL,
        normalHash VARCHAR(255) NOT NULL,
        saltHash VARCHAR(255) NOT NULL,
        pepperHash VARCHAR(255) NOT NULL,
        saltPepperHash VARCHAR(255) NOT NULL
    )
`
    );
    console.log('Database table initialized successfully.');
  } catch (error) {
    console.error('Error initializing database table:', error);
  } finally {
    client.release();
  }
}

// --- Data Types ---
interface UserDetails {
  username: string;
  salt: string;
  normalhash: string;
  salthash: string;
  pepperhash: string;
  saltpepperhash: string;
}

interface LoginRequest extends Request {
  body: {
    username: string;
    password: string;
  };
}

interface LoginResponse extends Response {
  send: (data: any) => this;
}

// --- Database Operations ---
async function insertIntoDb(user: UserDetails): Promise<void> {
  const client = await pool.connect();
  try {
    const result = await client.query(
      `INSERT INTO users (username, salt, normalHash, saltHash, pepperHash, saltPepperHash) VALUES ($1, $2, $3, $4, $5, $6)`,
      [user.username, user.salt, user.normalhash, user.salthash, user.pepperhash, user.saltpepperhash]
    );
    if (result.rowCount === 0) {
      throw new Error("Failed to insert user ${ user.username }");
    }
  } catch (error: any) {
    if (error.code === '23505') {
      throw new Error("Username ${ user.username } already exists");
    } else {
      console.error('Error inserting into database:', error);
      throw error;
    }
  } finally {
    client.release();
  }
}

async function getUser(username: string): Promise<UserDetails | null> {
  const client = await pool.connect();
  try {
    const result = await client.query(`SELECT * FROM users WHERE username = $1`, [username]);
    if (result.rows.length > 0) {
      return result.rows[0] as UserDetails;
    }
    return null;
  } catch (error) {
    console.error('Error fetching user from database:', error);
    return null;
  } finally {
    client.release();
  }
}

// --- Hashing Function ---
async function shaHash(algorithm: "SHA-1" | "SHA-256" | "SHA-384" | "SHA-512", data: string): Promise<string | null> {
  const textEncoder = new TextEncoder();
  const dataBuffer = textEncoder.encode(data);

  try {
    const hashBuffer = await crypto.subtle.digest(algorithm, dataBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray
      .map((b) => b.toString(16).padStart(2, "0"))
      .join("");
    return hashHex;
  } catch (error) {
    console.error("Error during hashing:", error);
    return null;
  }
}

// --- Authentication Handlers ---
const handleSignUp = async (req: LoginRequest, res: LoginResponse) => {
  console.log('Sign up request received', req.body);
  const { username, password } = req.body;
  const randomSalt = crypto.randomBytes(16).toString('hex');
  const normalHash = await shaHash('SHA-256', password);
  const saltHash = await shaHash('SHA-256', password + randomSalt);
  const pepperHash = await shaHash('SHA-256', secretKey + password);
  const saltPepperHash = await shaHash('SHA-256', secretKey + password + randomSalt);
  let success = false;
  let user: UserDetails | null = null;

  if (normalHash && saltHash && pepperHash && saltPepperHash) {
    user = {
      username,
      salt: randomSalt,
      normalhash: normalHash,
      salthash: saltHash,
      pepperhash: pepperHash,
      saltpepperhash: saltPepperHash,
    };
    success = true;
  }

  if (!success) {
    return res.send({
      success: false,
      error: 'Invalid credentials during signup',
    });
  }

  try {
    if (user) {
      await insertIntoDb(user);
      res.send({
        success,
        username,
        salt: randomSalt,
        normalHash,
        saltHash,
        pepperHash,
        saltPepperHash,
      });
    }
  } catch (error: any) {
    res.send({
      success: false,
      error: error.message,
    });
  }
};

const handleLogin = async (req: LoginRequest, res: LoginResponse) => {
  console.log('Login request received', req.body);
  const { username, password } = req.body;
  const user = await getUser(username);
  console.log('User', user);

  if (!user) {
    return res.send({ success: false, error: 'Invalid username or password' });
  } else {

    const providedNormalHash = await shaHash('SHA-256', password);
    const providedSaltHash = await shaHash('SHA-256', password + user.salt);
    const providedPepperHash = await shaHash('SHA-256', secretKey + password);
    const providedSaltPepperHash = await shaHash('SHA-256', secretKey + password + user.salt);

    const normalHashMatch = providedNormalHash === user.normalhash;
    const saltHashMatch = providedSaltHash === user.salthash;
    const pepperHashMatch = providedPepperHash === user.pepperhash;
    const saltPepperHashMatch = providedSaltPepperHash === user.saltpepperhash;

    console.log("Provided Normal Hash", providedNormalHash, "Stored Normal Hash", user.normalhash);
    console.log("Provided Salt Hash", providedSaltHash, "Stored Salt Hash", user.salthash);
    console.log("Provided Pepper Hash", providedPepperHash, "Stored Pepper Hash", user.pepperhash);
    console.log("Provided Salt Pepper Hash", providedSaltPepperHash, "Stored Salt Pepper Hash", user.saltpepperhash);

    res.send({
      success: normalHashMatch && saltHashMatch && pepperHashMatch && saltPepperHashMatch,
      normalHash: normalHashMatch,
      saltHash: saltHashMatch,
      pepperHash: pepperHashMatch,
      saltPepperHash: saltPepperHashMatch
    });
  }
};

// --- Route Definitions ---
app.post('/sign-up', handleSignUp);
app.post('/login', handleLogin);

// --- Server Startup ---
const startServer = async () => {
  await initializeDatabase();
  app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
  });
};

startServer();
