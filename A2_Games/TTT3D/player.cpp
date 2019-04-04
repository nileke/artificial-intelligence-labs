#include "player.hpp"
#include <cstdlib>
#include <unordered_map>
#define ROWS = 4;
#define COLUMNS = 4;
#define LAYERS = 4;

namespace TICTACTOE3D
{

    int winning_moves[76][4] = {
        // First layer
        { 0,   1,   2,   3  },
        { 4,   5,   6,   7  },
        { 8,   9,   10,  11 },
        { 12,  13,  14,  15 },
        { 0,   4,   8,   12 },
        { 1,   5,   9,   13 },
        { 2,   6,   10,  14 },
        { 3,   7,   11,  15 },
        { 0,   5,   10,  15 },
        { 3,   6,   9,   12 },
        // Second layer
        { 16,  17,  18,  19 },
        { 20,  21,  22,  23 },
        { 24,  25,  26,  27 },
        { 28,  29,  30,  31 },
        { 16,  20,  24,  28 },
        { 17,  21,  25,  29 },
        { 18,  22,  26,  30 },
        { 19,  23,  27,  31 },
        { 16,  21,  26,  31 },
        { 19,  22,  25,  28 },
        // Third layer
        { 32,  33,  34,  35 },
        { 36,  37,  38,  39 },
        { 40,  41,  42,  43 },
        { 44,  45,  46,  47 },
        { 32,  36,  40,  44 },
        { 33,  37,  41,  45 },
        { 34,  38,  42,  46 },
        { 35,  39,  43,  47 },
        { 32,  37,  42,  47 },
        { 35,  38,  41,  44 },
        { 48,  49,  50,  51 },
        { 52,  53,  54,  55 },
        { 56,  57,  58,  59 },
        { 60,  61,  62,  63 },
        // Special cases
        { 48,  52,  56,  60 },
        { 49,  53,  57,  61 },
        { 50,  54,  58,  62 },
        { 51,  55,  59,  63 },
        { 48,  53,  58,  63 },
        { 51,  54,  57,  60 },
        { 0,   16,  32,  48 },
        { 1,   17,  33,  49 },
        { 2,   18,  34,  50 },
        { 3,   19,  35,  51 },
        { 4,   20,  36,  52 },
        { 5,   21,  37,  53 },
        { 6,   22,  38,  54 },
        { 7,   23,  39,  55 },
        { 8,   24,  40,  56 },
        { 9,   25,  41,  57 },
        { 10,  26,  42,  58 },
        { 11,  27,  43,  59 },
        { 12,  28,  44,  60 },
        { 13,  29,  45,  61 },
        { 14,  30,  46,  62 },
        { 15,  31,  47,  63 },
        { 0,   17,  34,  51 },
        { 3,   18,  33,  48 },
        { 4,   21,  38,  55 },
        { 7,   22,  37,  52 },
        { 8,   25,  42,  59 },
        { 11,  26,  41,  56 },
        { 12,  29,  46,  63 },
        { 15,  30,  45,  60 },
        { 0,   21,  42,  63 },
        { 3,   22,  41,  60 },
        { 15,  26,  37,  48 },
        { 12,  25,  38,  51 },
        { 0,   20,  40,  60 },
        { 12,  24,  36,  48 },
        { 1,   21,  41,  61 },
        { 13,  25,  37,  49 },
        { 2,   22,  42,  62 },
        { 14,  26,  38,  50 },
        { 3,   23,  43,  63 },
        { 15,  27,  39,  51 },
};


/*
const std::array<std::vector<int>, 64> winning_moves = {
        std::vector<int>{ 0, 1, 2, 3, 0, 4, 8, 12, 0, 16, 32, 48, 0, 5, 10, 15, 0, 21, 42, 63, 0, 20, 40, 60, 0, 17, 34, 51},
        std::vector<int>{0, 1, 2, 3, 2, 6, 10, 14, 2, 18, 34, 50, 2, 22, 42, 62, 1, 21, 41, 61 },
        std::vector<int>{0, 1, 2, 3, 3, 7, 11, 15, 3, 19, 35, 51, 3, 23, 43, 63, 3, 18, 33, 48, 3, 22, 41, 60 },
        std::vector<int>{4, 5, 6, 7, 0, 4, 8, 12, 4, 21, 38, 55, 4, 20, 36, 52 },
        std::vector<int>{4, 5, 6, 7, 1, 5, 9, 13, 5, 21, 37, 53, 0, 5, 10, 15 },
        std::vector<int>{4, 5, 6, 7, 3, 6, 9, 12, 6, 10, 14, 18, 6, 22, 28, 34},
        std::vector<int>{4, 5, 6, 7, 3, 7, 11, 15, 7, 22, 27, 52, 7, 23, 39, 55 },
        std::vector<int>{8, 9, 10, 11, 0, 4, 8, 12, 8, 24, 40, 56, 8, 25, 42, 59 },
        std::vector<int>{8, 9, 10, 11, 1, 5, 9, 13, 9, 25, 41, 57, 3, 6, 9, 12, 3, 6, 9, 12 },
        std::vector<int>{ 8, 9, 10, 11, 2, 6, 10, 14, 10, 26, 42, 58 , 0, 5, 10, 15},
        std::vector<int>{ 8, 9, 10, 11, 3, 7, 11, 15, 11, 27, 43, 59, 11, 26, 41, 56, 4, 21, 38, 55 },
        std::vector<int>{ 12, 13, 14, 15, 0, 4, 8, 12, 12, 28, 44, 60, 3, 6, 9, 12, 12, 25, 38, 51, 12, 24, 36, 48},
        std::vector<int>{ 12, 13, 14, 15, 1, 5, 9, 13, 13, 29, 45, 61, 13, 25, 37, 49, 13, 25, 37, 49},
        std::vector<int>{ 12, 13, 14, 15, 2, 6, 10, 14, 14, 30, 46, 62, 14, 26, 38, 50},
        std::vector<int>{ 12, 13, 14, 15, 3, 7, 11, 15, 15, 31, 47, 63, 0, 5, 10, 15, 15, 26, 37, 48, 15, 27, 39, 51, 15, 30, 45, 60 },
        std::vector<int>{ 16, 17, 18, 19, 16, 20, 24, 28, 0, 16, 32, 48, 0, 16, 32, 48, 16,  21,  26,  31 },
        std::vector<int>{ 16, 17, 18, 19, 17, 21, 25, 29, 1, 17, 33, 49, 0, 17, 34, 51 },
        std::vector<int>{ 16, 17, 18, 19, 18, 22, 26, 30, 2, 18, 34, 50, 3, 18, 33, 48 },
        std::vector<int>{ 16, 17, 18, 19, 19, 23, 27, 31, 3, 19, 35, 51, 19, 22, 25, 28},
        std::vector<int>{ 20, 21, 22, 23, 16, 20, 24, 28, 4, 20, 36, 52, 0, 20, 40, 60 },
        std::vector<int>{ 20, 21, 22, 23, 17, 21, 25, 29, 5, 21, 37, 53, 1, 21, 41, 61 },
        std::vector<int>{ 20, 21, 22, 23, 18, 22, 26, 30, 6, 22, 38, 54, 19, 22, 25, 28, 3, 22, 41, 60 },
        std::vector<int>{ 20, 21, 22, 23, 19, 23, 27, 31, 7, 23, 39, 55, 3, 23, 43, 63 },
        std::vector<int>{ 24, 25, 26, 27, 16, 20, 24, 28, 8, 24, 40, 56, 12, 24, 36, 48 },
        std::vector<int>{ 24, 25, 26, 27, 17, 21, 25, 29, 9, 25, 41, 57, 13, 25, 37, 49, 12, 25, 38, 51 },
        std::vector<int>{ 24, 25, 26, 27, 18, 22, 26, 30, 10, 26, 42, 58, 14, 26, 38, 50, 15, 26, 37, 48, 11, 26, 41, 56},
        std::vector<int>{ 24, 25, 26, 27, 19, 23, 27, 31, 11, 27, 43, 59, 15, 27, 39, 51 },
        std::vector<int>{ 28, 29, 30, 31, 16, 20, 24, 28, 12, 28, 44, 60, 19, 22, 25, 28 },
        std::vector<int>{ 28, 29, 30, 31, 17, 21, 25, 29, 13, 29, 45, 61, 12, 29, 46, 63 },
        std::vector<int>{ 28, 29, 30, 31, 18, 22, 26, 30, 14, 30, 46, 62, 15, 30, 45, 60 },
        std::vector<int>{ 28, 29, 30, 31, 19, 23, 27, 31, 15, 31, 47, 63, 16, 21, 26, 31 },
        std::vector<int>{ 32, 33, 34, 35, 32, 36, 40, 44, 0, 16, 32, 48, 0, 16, 32, 48, 3, 18, 33, 48, 32,  37,  42,  47 },
        std::vector<int>{ 32, 33, 34, 35, 33, 37, 41, 45, 1, 17, 33, 49, 3, 18, 33, 48 },
        std::vector<int>{ 32, 33, 34, 35, 34, 38, 42, 46, 2, 18, 34, 50, 0, 17, 34, 51 },
        std::vector<int>{ 32, 33, 34, 35, 35, 39, 43, 47, 3, 19, 35, 51, 35, 38, 41, 44 },
        std::vector<int>{ 36, 37, 38, 39, 32, 36, 40, 44, 4, 20, 36, 52, 12, 24, 36, 48 },
        std::vector<int>{ 36, 37, 38, 39, 33, 37, 41, 45, 5, 21, 37, 53, 13, 25, 37, 49, 7, 22, 37, 52 },
        std::vector<int>{ 36, 37, 38, 39, 34, 38, 42, 46, 6, 22, 38, 54, 14, 26, 38, 50, 35, 38, 41, 44, 12, 25, 38, 51},
        std::vector<int>{ 36, 37, 38, 39, 35, 39, 43, 47, 7, 23, 39, 55, 15, 27, 39, 51 },
        std::vector<int>{ 40, 41, 42, 43, 32, 36, 40, 44, 8, 24, 40, 56, 0, 20, 40, 60 },
        std::vector<int>{ 40, 41, 42, 43, 33, 37, 41, 45, 9, 25, 41, 57, 35, 38, 41, 44, 3, 22, 41, 60, 1, 21, 41, 61 },
        std::vector<int>{ 40, 41, 42, 43, 34, 38, 42, 46, 10, 26, 42, 58, 2, 22, 42, 62 },
        std::vector<int>{ 40, 41, 42, 43, 35, 39, 43, 47, 11, 27, 43, 59, 3, 23, 43, 63 },
        std::vector<int>{ 44, 45, 46, 47, 32, 36, 40, 44, 12, 28, 44, 60, 35, 38, 41, 44 },
        std::vector<int>{ 44, 45, 46, 47, 33, 37, 41, 45, 13, 29, 45, 61, 15, 30, 45, 60 },
        std::vector<int>{ 44, 45, 46, 47, 34, 38, 42, 46, 14, 30, 46, 62, 12, 29, 46, 63 },
        std::vector<int>{ 44, 45, 46, 47, 35, 39, 43, 47, 15, 31, 47, 63, 32, 37, 42, 47 },
        std::vector<int>{ 48, 49, 50, 51, 48, 52, 56, 60, 0, 16, 32, 48, 0, 16, 32, 48, 3, 18, 33, 48, 48, 53, 58, 63, 15, 26, 37, 48, 12, 24, 36, 48 },
        std::vector<int>{ 48, 49, 50, 51, 49, 53, 57, 61, 1, 17, 33, 49, 13, 25, 37, 49 },
        std::vector<int>{ 48, 49, 50, 51, 50, 54, 58, 62, 2, 18, 34, 50, 14, 26, 38, 50 },
        std::vector<int>{ 48, 49, 50, 51, 51, 55, 59, 63, 3, 19, 35, 51, 0, 17, 34, 51, 12, 25, 38, 51, 15, 27, 39, 51 },
        std::vector<int>{ 52, 53, 54, 55, 48, 52, 56, 60, 4, 20, 36, 52, 7, 22, 37, 52 },
        std::vector<int>{ 52, 53, 54, 55, 49, 53, 57, 61, 5, 21, 37, 53, 48, 53, 58, 63 },
        std::vector<int>{ 52, 53, 54, 55, 50, 54, 58, 62, 6, 22, 38, 54, 51, 54, 57, 60 },
        std::vector<int>{ 52, 53, 54, 55, 51, 55, 59, 63, 7, 23, 39, 55, 4, 21, 38, 55 },
        std::vector<int>{ 56, 57, 58, 59, 48, 52, 56, 60, 8, 24, 40, 56, 11, 26, 41, 56 },
        std::vector<int>{ 56, 57, 58, 59, 49, 53, 57, 61, 9, 25, 41, 57, 51, 54, 57, 60 },
        std::vector<int>{ 56, 57, 58, 59, 50, 54, 58, 62, 10, 26, 42, 58, 48, 53, 58, 63 },
        std::vector<int>{ 56, 57, 58, 59, 51, 55, 59, 63, 11, 27, 43, 59, 8, 25, 42, 59 },
        std::vector<int>{ 60, 61, 62, 63, 48, 52, 56, 60, 12, 28, 44, 60, 0, 20, 40, 60, 60, 57, 54, 51, 3, 22, 41, 60, 51, 54, 57, 60},
        std::vector<int>{ 60, 61, 62, 63, 49, 53, 57, 61, 13, 29, 45, 61, 1, 21, 41, 61 },
        std::vector<int>{ 60, 61, 62, 63, 50, 54, 58, 62, 14, 30, 46, 62, 2, 22, 42, 62 },
        std::vector<int>{ 60, 61, 62, 63, 51, 55, 59, 63, 15, 31, 47, 63, 0, 21, 42, 63, 48, 53, 58, 48, 53, 58, 63, 3, 23, 43, 63, 12, 29, 46, 63 },
    };
    */

    // 0
    int index = -1;
    //const unsigned int BOARD_SIZE =  TICTACTOE3D::GameState::cSquares;

    int current_player = -1;
    int opponent = -1;




    int Evaluate(const GameState& state, int depth)
    {
        int score = 0;

        if (state.isXWin()) {
            if (current_player == CELL_X) return INT32_MAX - depth; // Maximize if X
            else return -INT32_MAX + depth; // Minimize if O
        }
        if (state.isOWin())
        {
            if (current_player == CELL_O) return INT32_MAX - depth; // Maximize if O since we're maximizing for current_player
            else return -INT32_MAX + depth; // Minimize if X
        }
        if (state.isDraw()) return 0;

        // Get the played index
        int player_move = state.getMove()[0];
        // Extract the corresponding winning moves from
        std::vector<int> *move = &winning_moves[player_move];
        int part_score = 0;
        int part_enemy_score = 0;
        int num_winning_moves = move->size() / 4;
        for (int i=0; i < num_winning_moves; i++) {
            part_score = 0;
            part_enemy_score = 0;
            for (int j=0; j < 4; j++) {
                int idx = (*move)[i*4+j];
                if (state.at(idx) == current_player) {
                    part_score++;
                }
                else if (state.at(idx) == opponent) {
                    part_enemy_score++;
                }
            }
            if (part_score == 0 && part_enemy_score != 0) {
                score -= part_enemy_score * part_enemy_score * part_enemy_score;
            }
            else if (part_enemy_score == 0 && part_score != 0 ) {
                score += part_score * part_score * part_score;
            }
        }

        return score;
    }

    int AlphaBeta(const GameState& state, bool maximize, int alpha, int beta, int depth, int current_depth)
    {
        current_depth++;
        if (state.isEOG() || depth == 0)
        {
            return Evaluate(state, current_depth);
        }

        std::vector<GameState> children;
        state.findPossibleMoves(children);

        int value;
        int best_index = -1;
        if (maximize)
        {
            value = -INT32_MAX;
            for (unsigned int i=0; i < children.size(); i++)
            {
                int score = AlphaBeta(children[i], !maximize, alpha, beta, depth-1, current_depth);
                if (value < score)
                {
                    value = score;
                    best_index = i;
                }

                alpha = std::max(alpha, value);
                if (alpha > beta) break; // beta prune
            }
        }
        else
        {
            value = INT32_MAX;
            for (unsigned int i=0; i < children.size(); i++)

            {
                int score = AlphaBeta(children[i], !maximize, alpha, beta, depth-1, current_depth);
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

GameState Player::play(const GameState &pState,const Deadline &pDue)
{
    //std::cerr << "Processing " << pState.toMessage() << std::endl;
    current_player = pState.getNextPlayer();
    opponent = current_player  == CELL_X ? CELL_X : CELL_O;

    std::vector<GameState> lNextStates;
    pState.findPossibleMoves(lNextStates);

    if (lNextStates.empty()) return GameState(pState, Move());

    int alpha = -INT32_MAX;
    int beta = INT32_MAX;

    bool maximize = true;
    int score = AlphaBeta(pState, maximize, alpha, beta, 2, 0);
    //std::cerr << "Result: " << score << " " << lNextStates[index].toMessage() << std::endl;

    return lNextStates[index];
}

/*namespace TICTACTOE3D*/ }

//cmake-build-debug/TTT3D init verbose < pipe | cmake-build-debug/TTT3D > pipe
