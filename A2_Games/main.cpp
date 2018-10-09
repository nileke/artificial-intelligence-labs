#include <iostream>

#include "TTT/gamestate.hpp"
#include "deadline.hpp"

using GameState = TICTACTOE::GameState;
using Deadline  = TICTACTOE::Deadline;
using Move = TICTACTOE::Move;


constexpr unsigned int A = 1;
constexpr unsigned int B = 2;

constexpr unsigned int ROWS    = 4;
constexpr unsigned int COLUMNS = 4;

constexpr unsigned int WINNING_STREAK = 4;


int CountVertical(const GameState& state, int player, int column)
{
    int count = 0;
    for (unsigned int row = 0; row < ROWS; ++row)
        if (state.at(row, column) != player)
            ++count;
    return count;
}
int CountHorizontal(const GameState& state, int player, int row)
{
    int count = 0;
    for (unsigned int column = 0; column < COLUMNS; ++column)
        if (state.at(row, column) != player)
            ++count;
    return count;
}
int CountLeftDiagonal(const GameState& state, int player)
{
    int count = 0;
    for (unsigned int row = 0, column = 0; column < COLUMNS; ++column, ++row)
        if (state.at(row, column) != player)
            ++count;
    return count;
}
int CountRightDiagonal(const GameState& state, int player)
{
    int count = 0;
    for (unsigned int row = 0, column = COLUMNS; column < COLUMNS; --column, ++row)
        if (state.at(row, column) != player)
            ++count;
    return count;
}

std::vector<GameState> TerminalStates(const GameState& state, int player)
{
    std::vector<GameState> result;

    std::vector<GameState> next_states;
    state.findPossibleMoves(next_states);

    for (auto& next_state : next_states)
    {
        for (unsigned int column = 0; column < COLUMNS; ++column)
            if (CountVertical(state, player, column) == WINNING_STREAK)
            {
                result.push_back(next_state);
                goto loop_end;
            }
        for (unsigned int row = 0; row < ROWS; ++row)
            if (CountHorizontal(state, player, row) == WINNING_STREAK)
            {
                result.push_back(next_state);
                goto loop_end;
            }
        if (CountLeftDiagonal(state, player) == WINNING_STREAK)
        {
            result.push_back(next_state);
            goto loop_end;
        }
        if (CountRightDiagonal(state, player) == WINNING_STREAK)
        {
            result.push_back(next_state);
            goto loop_end;
        }
            
        loop_end:;
    }

    return result;
}

int Evaluate(const GameState& state, int player)
{
    int score = 0;

    for (unsigned int column = 0; column < COLUMNS; ++column) score += CountVertical(state, player, column);
    for (unsigned int row = 0;    row < ROWS;       ++row)    score += CountHorizontal(state, player, row);
    score += CountLeftDiagonal(state, player);
    score += CountRightDiagonal(state, player);

    return score;
}


int MiniMax(const GameState& state, int player, int depth)
{

    auto terminal_states = TerminalStates(state, player);

    if (depth == 0 || !terminal_states.empty())
        return Evaluate(state, player);

    if (player == A)
    {
        int best = -INT32_MAX;
        for (auto& child : terminal_states)
        {
            auto v = MiniMax(child, B, depth-1);
            best = std::max(best, v);
        }
        return best;
    }
    else if (player == B)
    {
        int best = INT32_MAX;
        for (auto& child : terminal_states)
        {
            auto v = MiniMax(child, A, depth-1);
            best = std::min(best, v);
        }
        return best;
    }
    else
    {
        std::cerr << "Something is wrong!" << std::endl;
        return {};
    }
}



GameState Player::play(const GameState& current_state, const Deadline& deadline)
{
    // Deadline is the time we have to complete our calculation before the game terminates.


    //std::cerr << "Processing " << pState.toMessage() << std::endl;

    std::vector<GameState> next_states;
    current_state.findPossibleMoves(next_states);

    // No moves can be done.
    if (next_states.empty())
        return GameState(current_state, Move());


    int best = 0;
    for (unsigned int i = 0; i < next_states.size(); ++i)
    {
        const GameState& state = next_states[i];
        auto result = MiniMax(state, 1);
        if (result > best)
            best = i;
    }

    return next_states[best];
}


