#include "player.hpp"
#include <cstdlib>
#include <algorithm>

namespace TICTACTOE
{

    constexpr unsigned int ROWS    = 4;
    constexpr unsigned int COLUMNS = 4;

    int current_player = -1;
    int index = -1;

    int VerticalHeuristic(const GameState& state)
    {
        int score = 0;

        for (unsigned int column = 0; column < COLUMNS; ++column)
        {
            int num = 0;
            for (unsigned int row = 0; row < ROWS; ++row)
            {
                if (state.at(row, column) == current_player)
                {
                    ++num;
                }
            }

            score += num * num;
        }

        return score;
    }
    int HorizontalHeuristic(const GameState& state)
    {
        int score = 0;

        for (unsigned int row = 0; row < ROWS; ++row)
        {
            int num = 0;
            for (unsigned int column = 0; column < COLUMNS; ++column)
            {
                if (state.at(row, column) == current_player)
                {
                    ++num;
                }
            }
            score += num * num;
        }

        return score;
    }
    int RightDiagonalHeuristic(const GameState& state)
    {
        //if ((int) state.getMove()[0] % 5 != 0) return 0;

        int num = 0;
        for (unsigned int column = 0, row = 0; column < COLUMNS; ++column, ++row)
        {
            if (state.at(row, column) == current_player)
            {
                ++num;
            }
        }

        return num * num;
    }
    int LeftDiagonalHeruristic(const GameState& state)
    {
        // if ((int) state.getMove()[0] % 3 != 0 || (int) state.getMove()[0] == 15) return 0;

        int num = 0;
        for (unsigned int column = 0, row = ROWS-1; column < COLUMNS; ++column, --row)
        {
            if (state.at(row, column) == current_player)
            {
                ++num;
            }
        }

        return num * num;
    }



    int Evaluate(const GameState& state)
    {
        int score = 0;

        if (state.isXWin()) {
            if (current_player == CELL_X) return 100; // Maximize if X
            else return -100; // Minimize if O
        }
        if (state.isOWin())
        {
            if (current_player == CELL_O) return 100; // Maximize if O since we're maximizing for current_player
            else return -100; // Minimize if X
        }
        if (state.isDraw()) return 0;

        score = VerticalHeuristic(state);
        score += HorizontalHeuristic(state);
        score += LeftDiagonalHeruristic(state);
        score += RightDiagonalHeuristic(state);

        return score;
    }


    int AlphaBeta(const GameState& state, bool maximize, int alpha, int beta, int depth)
    {
        if (state.isEOG() || depth == 0)
        {
            return Evaluate(state);
        }

        std::vector<GameState> children;
        state.findPossibleMoves(children);


        int value;
        int best_index = -1;
        if (maximize)
        {
            value = -INT32_MAX;
            //for (auto& child : children)
            for (unsigned int i=0; i < children.size(); i++)
            {
                int score = AlphaBeta(children[i], !maximize, alpha, beta, depth-1);
                if (value < score)
                {
                    value = score;
                    best_index = i;
                }
                //value = std::max(value, score);

                alpha = std::max(alpha, value);
                if (alpha > beta) break; // beta prune
            }
        }
        else
        {
            value = INT32_MAX;
            // for (auto& child : children)
            for (unsigned int i=0; i < children.size(); i++)

            {
                int score = AlphaBeta(children[i], !maximize, alpha, beta, depth-1);
                if (value > score)
                {
                    value = score;
                    best_index = i;
                }

                beta = std::min(beta, value);
                if (alpha >= beta) break; // alpha prune
            }
        }
        index = best_index;
        //std::cerr << "Result: " << result << " " << state.toMessage() << std::endl;
        return value;
    }



    GameState Player::play(const GameState& current_state, const Deadline& deadline)
    {
        // Deadline is the time we have to complete our calculation before the game terminates.
        //std::cerr << "Processing " << current_state.toMessage() << std::endl;

        current_player = current_state.getNextPlayer();

        std::vector<GameState> next_states;
        current_state.findPossibleMoves(next_states);


        // No moves can be done.
        if (next_states.empty())
        {
            return GameState(current_state, Move());
        }

        int alpha = -INT32_MAX;
        int beta = INT32_MAX;

        bool maximize = true;
        int score = AlphaBeta(current_state, maximize, alpha, beta, 6);
        std::cerr << "Result: " << score << " " << next_states[index].toMessage() << std::endl;

        return next_states[index];
    }

/*namespace TICTACTOE*/ }


// cmake-build-debug/TTT init verbose < pipe | cmake-build-debug/TTT > pipe