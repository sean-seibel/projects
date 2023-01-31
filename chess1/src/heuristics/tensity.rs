use std::cmp::max;
use std::fmt::{Debug, Display, Formatter, Write};
use crate::heuristics::tensity::Attack::{GainfulAttack, NoAttack, Trade};
use crate::piece::{Color, Piece};
use crate::{White, Black};
use crate::piece::PieceType::King;

#[derive(Clone, Copy)]
#[derive(PartialEq)]
pub enum Attack {
    NoAttack,
    Trade,
    GainfulAttack,
}

impl Debug for Attack {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        f.write_str(
            match self {
                NoAttack => "-",
                Trade => "T",
                GainfulAttack => "G",
            }
        )
    }
}

//positive = more tense
pub fn tension_against_color(vals: &[i32; 4], color: Color) -> i32 {
    match color {
        Color::None => 0,
        White => {
            vals[0]/3 + vals[1]
        }
        Color::Black => {
            vals[2]/3 + vals[3]
        }
    }
}

pub fn total_tension(vals: &[i32; 4]) -> i32 {
    tension_against_color(vals, White) + tension_against_color(vals, Black)
}

//num trades available against white pieces, num white pieces en prise, then same for black
pub fn tensions(board: &[[Piece;8];8], winfs: &[[i32;8];8], binfs: &[[i32;8];8]) -> [i32; 4] {
    let mut num = [0i32;4];

    for r in 0..8 {
        for f in 0..8 {
            let piece = board[r][f];

            match match piece.color() {
                Color::None => NoAttack,
                Color::White => board[r][f].under_attack_e(winfs[r][f], binfs[r][f].abs()),
                Color::Black => board[r][f].under_attack_e(binfs[r][f].abs(), winfs[r][f]),
            } {
                NoAttack => {}
                Trade => num[if piece.color()==White{0}else{2}] += 1,
                GainfulAttack => num[if piece.color()==White{1}else{3}] += 1,
            }
        }
    }

    num
}

pub fn tension_board(board: &[[Piece;8];8], winfs: &[[i32;8];8], binfs: &[[i32;8];8]) -> [[Attack;8];8] {
    let mut b = [[NoAttack;8];8];

    for r in 0..8 {
        for f in 0..8 {
            b[r][f] = match board[r][f].color() {
                Color::None => NoAttack,
                Color::White => board[r][f].under_attack_e(winfs[r][f], binfs[r][f].abs()),
                Color::Black => board[r][f].under_attack_e(binfs[r][f].abs(), winfs[r][f]),
            }
        }
    }

    b
}

pub fn threat_value(board: &[[Piece;8];8], threats: [[Attack;8];8], to_move: Color) -> i32 {
    let mut threats_on_white: Vec<i32> = vec![];
    let mut threats_on_black: Vec<i32> = vec![];
    for r in 0..8 {
        for f in 0..8 {
            if threats[r][f] == GainfulAttack {
                let piece = board[r][f];
                if piece.piece_type() == King { continue; }
                match piece.color() {
                    Color::None => continue,
                    White => &mut threats_on_white,
                    Black => &mut threats_on_black,
                }.push(piece.material_eval(r as u8, f as u8).abs());
            }
        }
    }

    match to_move {
        Color::None => return 0,
        White => { threats_on_white.sort(); threats_on_white.pop(); },
        Black => { threats_on_black.sort(); threats_on_black.pop(); },
    }

    threats_on_black.iter().sum::<i32>() - threats_on_white.iter().sum::<i32>()
}

impl Piece {

    //always receives N+
    pub fn under_attack_e(&self, sminf_defending: i32, sminf_attacking: i32) -> Attack {
        let own_sminf_value = self.sminf().abs();
        if sminf_attacking == 0 { return NoAttack; }
        //if sminf_defending == 0 { return GainfulAttack; }
        let without_cheapest_attacker = remove_cheapest_attacker(sminf_attacking as u32);
        let cheapest_attacker_value = sminf_attacking as u32 - without_cheapest_attacker;
        if cheapest_attacker_value > own_sminf_value as u32 || without_cheapest_attacker >= sminf_defending as u32 {
            return GainfulAttack
        }
        if sminf_attacking >= own_sminf_value { return Trade }
        NoAttack
    }
}

pub fn remove_cheapest_attacker(sminf: u32) -> u32 {
    // examine this: 00 00 00 00 00 00
    // and subtract 1 from the first bit double that has anything
    let mut test = sminf;
    let mut index = 0;
    while test > 1 {
        test >>= 1;
        index += 1;
    }

    sminf - (1 << index)
}

