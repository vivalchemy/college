:- discontiguous can_reach/2, get_on/2, under/2, is_close/2.

% Production rules:
can_reach(X, Y) :- clever(X), is_close(X, Y).
get_on(X, Y) :- can_climb(X, Y).

under(Y, Z) :- in_room(Y), in_room(Z), can_climb(_, Y).
is_close(X, Z) :- get_on(X, Y), under(Y, Z).
is_close(_, Y) :- tall(Y).

% Facts:
in_room(bananas).
in_room(chair).
in_room(monkey).

clever(monkey).
can_climb(monkey, chair).
tall(chair).
can_move(monkey, chair, bananas).