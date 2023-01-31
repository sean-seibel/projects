use std::cmp::{max, min};
use crate::piece::Piece;
use crate::piece::Piece::*;


//0-100
pub fn in_endgame(board: &[[Piece;8];8]) -> i32 {
    let mut w_mat = 0;
    let mut b_mat = 0;
    for r in 0..8 {
        for f in 0..8 {
            let mat = board[r][f].material_eval_base();
            match board[r][f] {
                BQueen|BRook|BBishop|BKnight => b_mat += mat,
                WQueen|WRook|WBishop|WKnight => w_mat += mat,
                _ => {}
            }
        }
    }
    // max 10 + 5*2 + 3*4 = 32
    // 32 +
    //    |
    //    |xx
    //    |xxxx
    //    |xxxxxx
    //  0 +–––––––––+
    //    0         32
    // 22000, 32000
    let mat_total = w_mat/1000 + b_mat/1000;
    let eg_quant = mat_total * 200 / 64 - 50;

    100 - max(0, min(eg_quant, 100))
}