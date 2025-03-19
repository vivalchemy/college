import math
import time

import streamlit as st

# Set page config
st.set_page_config(page_title="Tic Tac Toe AI", layout="wide")

# Improved CSS for square cells with proper button alignment and reduced size
st.markdown(
    """
<style>
.square-button {
    aspect-ratio: 1 / 1;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    font-size: 1.5rem !important;
    font-weight: bold !important;
    height: 100% !important;
    width: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
}
.stButton button {
    height: 100% !important;
    width: 100% !important;
    aspect-ratio: 1 / 1;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    font-size: 1.5rem !important;
    font-weight: bold !important;
    padding: 0 !important;
}

.game-container {
    max-width: 150px;
    margin: 0 auto;
}
.cell-container {
    aspect-ratio: 1 / 1;
    width: 100%;
    height: 100%;
    padding: 0;
}
.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}
.title-container {
    margin: 0 !important;
}
#rectangle-button {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    font-size: 1.5rem !important;
    font-weight: bold !important;
    width: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
}
</style>
""",
    unsafe_allow_html=True,
)

# Initialize session state
if "board" not in st.session_state:
    st.session_state.board = [[" " for _ in range(3)] for _ in range(3)]
if "game_over" not in st.session_state:
    st.session_state.game_over = False
if "winner" not in st.session_state:
    st.session_state.winner = None
if "winning_cells" not in st.session_state:
    st.session_state.winning_cells = []
if "player_score" not in st.session_state:
    st.session_state.player_score = 0
if "ai_score" not in st.session_state:
    st.session_state.ai_score = 0
if "draws" not in st.session_state:
    st.session_state.draws = 0
if "difficulty" not in st.session_state:
    st.session_state.difficulty = "Hard"
if "ai_goes_first" not in st.session_state:
    st.session_state.ai_goes_first = False
if "current_turn" not in st.session_state:
    st.session_state.current_turn = "AI" if st.session_state.ai_goes_first else "Player"
if "ai_scores" not in st.session_state:
    st.session_state.ai_scores = [[None for _ in range(3)] for _ in range(3)]


# Check for winning combinations
def check_winner(board):
    # Check rows
    for i in range(3):
        if board[i][0] == board[i][1] == board[i][2] != " ":
            return board[i][0], [(i, 0), (i, 1), (i, 2)]

    # Check columns
    for i in range(3):
        if board[0][i] == board[1][i] == board[2][i] != " ":
            return board[0][i], [(0, i), (1, i), (2, i)]

    # Check diagonals
    if board[0][0] == board[1][1] == board[2][2] != " ":
        return board[0][0], [(0, 0), (1, 1), (2, 2)]

    if board[0][2] == board[1][1] == board[2][0] != " ":
        return board[0][2], [(0, 2), (1, 1), (2, 0)]

    return None, []


# Evaluate board for minimax algorithm
def evaluate(board):
    winner, _ = check_winner(board)
    if winner == "X":
        return 10
    elif winner == "O":
        return -10
    return 0


# Check if there are moves left
def is_moves_left(board):
    for row in board:
        if " " in row:
            return True
    return False


# Minimax with Alpha-Beta Pruning and limited depth for easier difficulties
def minimax(board, depth, max_depth, is_maximizing, alpha, beta):
    score = evaluate(board)

    # Terminal conditions
    if score == 10:
        return score - depth
    if score == -10:
        return score + depth
    if not is_moves_left(board) or depth == max_depth:
        return 0

    if is_maximizing:
        best = -math.inf
        for i in range(3):
            for j in range(3):
                if board[i][j] == " ":
                    board[i][j] = "X"
                    best = max(
                        best, minimax(board, depth + 1, max_depth, False, alpha, beta)
                    )
                    board[i][j] = " "
                    alpha = max(alpha, best)
                    if beta <= alpha:
                        break
        return best
    else:
        best = math.inf
        for i in range(3):
            for j in range(3):
                if board[i][j] == " ":
                    board[i][j] = "O"
                    best = min(
                        best, minimax(board, depth + 1, max_depth, True, alpha, beta)
                    )
                    board[i][j] = " "
                    beta = min(beta, best)
                    if beta <= alpha:
                        break
        return best


# Find the best move with different difficulty levels
def find_best_move():
    # Set max depth based on difficulty
    if st.session_state.difficulty == "Easy":
        max_depth = 1
    elif st.session_state.difficulty == "Medium":
        max_depth = 3
    else:  # Hard
        max_depth = 9

    best_val = -math.inf
    best_move = (-1, -1)
    scores = [[None for _ in range(3)] for _ in range(3)]

    # Introduce randomness for easier difficulties
    import random

    moves = []
    for i in range(3):
        for j in range(3):
            if st.session_state.board[i][j] == " ":
                moves.append((i, j))

    # For Easy difficulty, sometimes make a random move
    if st.session_state.difficulty == "Easy" and random.random() < 0.4:
        return random.choice(moves), scores

    # For Medium difficulty, sometimes make a slightly suboptimal move
    make_suboptimal = st.session_state.difficulty == "Medium" and random.random() < 0.3

    for i in range(3):
        for j in range(3):
            if st.session_state.board[i][j] == " ":
                st.session_state.board[i][j] = "X"
                move_val = minimax(
                    st.session_state.board, 0, max_depth, False, -math.inf, math.inf
                )
                st.session_state.board[i][j] = " "

                scores[i][j] = move_val

                if move_val > best_val:
                    best_val = move_val
                    best_move = (i, j)

    # For Medium difficulty, sometimes choose a suboptimal move
    if make_suboptimal and len(moves) > 1:
        suboptimal_moves = [m for m in moves if m != best_move]
        if suboptimal_moves:
            return random.choice(suboptimal_moves), scores

    return best_move, scores


# Handle the player click
def handle_click(row, col):
    if st.session_state.game_over:
        return

    if (
        st.session_state.board[row][col] == " "
        and st.session_state.current_turn == "Player"
    ):
        st.session_state.board[row][col] = "O"
        st.session_state.current_turn = "AI"
        st.session_state.ai_scores = [[None for _ in range(3)] for _ in range(3)]

        # Check if player won
        winner, winning_cells = check_winner(st.session_state.board)
        if winner == "O":
            st.session_state.winner = "Player"
            st.session_state.winning_cells = winning_cells
            st.session_state.game_over = True
            st.session_state.player_score += 1
            return

        if not is_moves_left(st.session_state.board):
            st.session_state.game_over = True
            st.session_state.winner = "Draw"
            st.session_state.draws += 1
            return


# AI's turn
def ai_move():
    if st.session_state.game_over or st.session_state.current_turn != "AI":
        return

    with st.spinner("AI is thinking..."):
        time.sleep(0.5)  # Shorter delay

    best_move, scores = find_best_move()
    st.session_state.ai_scores = scores
    x, y = best_move

    if x != -1 and y != -1:
        st.session_state.board[x][y] = "X"
        st.session_state.current_turn = "Player"

        # Check if AI won
        winner, winning_cells = check_winner(st.session_state.board)
        if winner == "X":
            st.session_state.winner = "AI"
            st.session_state.winning_cells = winning_cells
            st.session_state.game_over = True
            st.session_state.ai_score += 1
            return

        if not is_moves_left(st.session_state.board):
            st.session_state.game_over = True
            st.session_state.winner = "Draw"
            st.session_state.draws += 1


# Reset the game
def reset_game():
    st.session_state.board = [[" " for _ in range(3)] for _ in range(3)]
    st.session_state.game_over = False
    st.session_state.winner = None
    st.session_state.winning_cells = []
    st.session_state.ai_scores = [[None for _ in range(3)] for _ in range(3)]

    # Change who goes first based on toggle
    st.session_state.current_turn = "AI" if st.session_state.ai_goes_first else "Player"


# Change difficulty
def change_difficulty(difficulty):
    st.session_state.difficulty = difficulty
    reset_game()


# Toggle AI first
def toggle_ai_first():
    st.session_state.ai_goes_first = not st.session_state.ai_goes_first
    reset_game()


# Reset scores
def reset_scores():
    st.session_state.player_score = 0
    st.session_state.ai_score = 0
    st.session_state.draws = 0


# UI Layout - Two main columns for game and analysis
game_col, analysis_col = st.columns([3, 2])

# Game Column
with game_col:
    # Header with title and new game button side by side
    title_col, new_game_col = st.columns([3, 1])
    with title_col:
        st.markdown(
            '<div class="title-container"><h1>🎮 Tic Tac Toe</h1></div>',
            unsafe_allow_html=True,
        )
    with new_game_col:
        st.markdown('<div id="rectangle-button">', unsafe_allow_html=True)
        st.button("🔄 New Game", key="new_game", on_click=reset_game)
        st.markdown("</div>", unsafe_allow_html=True)

    # Game status display
    if st.session_state.game_over:
        if st.session_state.winner == "Player":
            st.success("🎉 You Win! 🎉")
        elif st.session_state.winner == "AI":
            st.error("🤖 AI Wins!")
        else:
            st.warning("🤝 Draw!")
    else:
        # Display whose turn it is
        turn_indicator = (
            "🤖 AI's Turn" if st.session_state.current_turn == "AI" else "👤 Your Turn"
        )
        st.info(turn_indicator)

    # Game board - improved layout with proper button placement
    st.markdown('<div class="game-container">', unsafe_allow_html=True)
    for i in range(3):
        cols = st.columns(3)
        for j in range(3):
            with cols[j]:
                cell_content = st.session_state.board[i][j]
                is_winning_cell = (i, j) in st.session_state.winning_cells

                # Create cell with properly aligned button
                if cell_content == " " and not st.session_state.game_over:
                    # Empty cell and game not over - show clickable button
                    st.button(
                        " ",
                        key=f"{i}-{j}",
                        on_click=handle_click,
                        args=(i, j),
                    )
                else:
                    # Display cell content
                    if cell_content == "X":
                        display_content = "❌"
                        if is_winning_cell:
                            st.markdown(
                                f'<div class="square-button" style="background-color:#a8f0a8;">{display_content}</div>',
                                unsafe_allow_html=True,
                            )
                        else:
                            st.markdown(
                                f'<div class="square-button">{display_content}</div>',
                                unsafe_allow_html=True,
                            )
                    elif cell_content == "O":
                        display_content = "⭕"
                        if is_winning_cell:
                            st.markdown(
                                f'<div class="square-button" style="background-color:#a8f0a8;">{display_content}</div>',
                                unsafe_allow_html=True,
                            )
                        else:
                            st.markdown(
                                f'<div class="square-button">{display_content}</div>',
                                unsafe_allow_html=True,
                            )
                    else:
                        # Empty cell with game over
                        st.markdown(
                            f'<div class="square-button"> </div>',
                            unsafe_allow_html=True,
                        )
    st.markdown("</div>", unsafe_allow_html=True)

# Analysis Column
with analysis_col:
    st.subheader("Game Analysis")

    # Scoreboard in a more compact format
    scores_cols = st.columns(3)
    with scores_cols[0]:
        st.metric("Player (⭕)", st.session_state.player_score)
    with scores_cols[1]:
        st.metric("AI (❌)", st.session_state.ai_score)
    with scores_cols[2]:
        st.metric("Draws", st.session_state.draws)

    # Game settings
    st.subheader("Settings")

    # Difficulty as radio buttons
    difficulty = st.radio(
        "Difficulty Level",
        ["Easy", "Medium", "Hard"],
        index=["Easy", "Medium", "Hard"].index(st.session_state.difficulty),
        horizontal=True,
        on_change=change_difficulty,
        args=(st.session_state.difficulty,),
    )
    if difficulty != st.session_state.difficulty:
        change_difficulty(difficulty)

    # Who goes first
    first_player = st.radio(
        "First Player",
        ["Player", "AI"],
        index=1 if st.session_state.ai_goes_first else 0,
        horizontal=True,
    )
    if (first_player == "AI" and not st.session_state.ai_goes_first) or (
        first_player == "Player" and st.session_state.ai_goes_first
    ):
        toggle_ai_first()

    # AI move analysis
    if not st.session_state.game_over:
        st.subheader("AI Move Evaluation")
        st.caption("Positive scores favor AI (❌), negative favor Player (⭕)")

        # Recalculate and display scores
        _, scores = find_best_move()
        st.session_state.ai_scores = scores

        # Display the scores in a grid
        for i in range(3):
            score_cols = st.columns(3)
            for j in range(3):
                with score_cols[j]:
                    cell_content = st.session_state.board[i][j]
                    score = st.session_state.ai_scores[i][j]

                    # Show the cell with score
                    if cell_content == "X":
                        symbol = "❌"
                    elif cell_content == "O":
                        symbol = "⭕"
                    else:
                        symbol = ""

                    if score is not None:
                        color = "red" if score > 0 else "blue" if score < 0 else "gray"
                        st.markdown(
                            f"<div style='text-align:center; padding:8px; border:1px solid #ddd; border-radius:4px;'>{symbol} <span style='color:{color};'>{score}</span></div>",
                            unsafe_allow_html=True,
                        )
                    else:
                        st.markdown(
                            f"<div style='text-align:center; padding:8px; border:1px solid #ddd; border-radius:4px;'>{symbol}</div>",
                            unsafe_allow_html=True,
                        )


# Make AI move if it's the AI's turn
if st.session_state.current_turn == "AI" and not st.session_state.game_over:
    ai_move()
    st.rerun()
