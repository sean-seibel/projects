use std::thread::sleep;
use std::time::Duration;
use crate::{Black, White};
use crate::piece::*;
use crate::piece::GameOver::{Checkmate, Draw, NotOver};
use crate::piece::Piece::*;
use crate::piece::PieceType::{Bishop, King, Knight, Pawn, Queen, Rook};

#[derive(Clone)]
pub struct Board {
    pub initial: [[Piece; 8]; 8],
    pub state: [[Piece; 8]; 8],
    pub history: Vec<Move>,
    pub castle_rights: u8,
    pub active_color: Color,
}

#[derive(Copy, Clone)]
#[derive(Eq, PartialEq)]
pub struct Square {
    pub rank: u8,
    pub file: u8,
}

#[derive(Copy, Clone)]
pub struct Move {
    pub from: Square,
    pub to: Square,
}

impl Board {
    // handles castling, en passant, promotion
    fn check_for_side_effects(board: &mut [[Piece; 8]; 8], mov: &Move) -> Piece {
        let piece = board[mov.from.rank as usize][mov.from.file as usize];
        if !(piece == WPawn || piece == BPawn || piece == WKing || piece == BKing ) { return piece; }
        let target = board[mov.to.rank as usize][mov.to.file as usize];
        if piece == WPawn && target == Empty {
            if mov.to.rank == 7 {
                board[6][mov.to.file as usize] = WQueen; return WQueen;
            } else if mov.to.rank != 0 {
            board[mov.to.rank as usize - 1][mov.to.file as usize] = Empty; }
            return piece;
        }
        if piece == BPawn && target == Empty {
            if mov.to.rank == 0 {
                board[1][mov.to.file as usize] = BQueen; return BQueen;
            } else if mov.to.rank as usize != 7 {
            board[mov.to.rank as usize + 1][mov.to.file as usize] = Empty; }
            return piece;
        }
        if piece == WKing && mov.from == (Square { rank: 0, file: 4 }) {
            if mov.to == (Square { rank: 0, file: 6 }) {
                board[0][7] = Empty;
                board[0][5] = WRook;
            } else if mov.to == (Square { rank: 0, file: 2 }) {
                board[0][0] = Empty;
                board[0][3] = WRook;
            }; return piece;
        }
        if piece == BKing && mov.from == (Square { rank: 7, file: 4 }) {
            if mov.to == (Square { rank: 7, file: 6 }) {
                board[7][7] = Empty;
                board[7][5] = BRook;
            } else if mov.to == (Square { rank: 7, file: 2 }) {
                board[7][0] = Empty;
                board[7][3] = BRook;
            };
        }
        piece
    }

    pub fn make_move(&mut self, mov: Move) {
        match mov.from {
            Square { rank: 0, file: 4 } => self.castle_rights &= !(WQUEENSIDE | WKINGSIDE),
            Square { rank: 7, file: 4 } => self.castle_rights &= !(BQUEENSIDE | BKINGSIDE),
            Square { rank: 0, file: 0 } => self.castle_rights &= !WQUEENSIDE,
            Square { rank: 7, file: 0 } => self.castle_rights &= !BQUEENSIDE,
            Square { rank: 0, file: 7 } => self.castle_rights &= !WKINGSIDE,
            Square { rank: 7, file: 7 } => self.castle_rights &= !BKINGSIDE,
            _ => {}
        }

        self.history.push(mov);

        let piece = Board::check_for_side_effects(&mut self.state, &mov);
        self.state[mov.to.rank as usize][mov.to.file as usize] = piece;
        self.state[mov.from.rank as usize][mov.from.file as usize] = Empty;

        self.active_color = self.active_color.switch();
    }

    pub fn piece_at(&self, sq: Square) -> Piece {
        self.state[sq.rank as usize][sq.file as usize]
    }

    pub fn check_move(&self, mov: Move, active: Color) -> bool {
        let piece = self.piece_at(mov.from);
        let target = self.piece_at(mov.to);

        if mov.from == mov.to { return false; }
        if piece.color() != active || piece == Empty { return false; }
        if target.color() == piece.color() { return false; }

        let test_board = { let mut tb = self.clone();
            tb.make_move(mov);
            tb.state };
        if Self::in_check(&test_board, active) { return false; }


        let rdiff = mov.from.file.abs_diff(mov.to.file);
        let fdiff = mov.from.rank.abs_diff(mov.to.rank);

        match piece {
            WKing | BKing => rdiff <= 1 && fdiff <= 1 || self.castle_check(mov, piece.color(), &self.state),
            WKnight | BKnight => (rdiff == 2 && fdiff == 1) || (rdiff == 1 && fdiff == 2),
            WBishop | BBishop => (rdiff == fdiff) && self.nothing_between(mov),
            WRook | BRook => (rdiff == 0 || fdiff == 0) && self.nothing_between(mov),
            WQueen | BQueen => (rdiff == 0 || fdiff == 0 || rdiff == fdiff) && self.nothing_between(mov),
            WPawn => self.valid_wpawn(mov),
            BPawn => self.valid_bpawn(mov),
            Empty => false,
        }
    }

    fn castle_check(&self, mov: Move, color: Color, current: &[[Piece; 8]; 8]) -> bool {
        let wks = self.castle_rights & WKINGSIDE > 0;
        let wqs = self.castle_rights & WQUEENSIDE > 0;
        let bks = self.castle_rights & BKINGSIDE > 0;
        let bqs = self.castle_rights & BQUEENSIDE > 0;
        self.nothing_between(mov) &&
        !Self::under_attack_by(current, if color==White { Black } else { White }, mov.from) &&
        match color {
            Color::None => false,
            White => {
                mov.from == Square { rank: 0, file: 4 } &&
                ((wqs && mov.to == Square { rank: 0, file: 2 } &&
                    current[0][0] == WRook &&
                    !Self::under_attack_by(current, Black, Square { rank: 0, file: 3 }))
                ||
                (wks && mov.to == Square { rank: 0, file: 6 } &&
                    current[0][7] == WRook &&
                    !Self::under_attack_by(current, Black, Square { rank: 0, file: 5 })))
            }
            Black => {
                mov.from == Square { rank: 7, file: 4 } &&
                    ((bqs && mov.to == Square { rank: 7, file: 2 } &&
                        current[7][0] == BRook &&
                        !Self::under_attack_by(current, White, Square { rank: 7, file: 3 }))
                    ||
                    (bks && mov.to == Square { rank: 7, file: 6 } &&
                        current[7][7] == BRook &&
                        !Self::under_attack_by(current, White, Square { rank: 7, file: 5 })))
            }
        }
    }

    pub(crate) fn in_check(board: &[[Piece; 8]; 8], color: Color) -> bool {
        let target_piece = if color==White { WKing } else { BKing };
        let mut rank = 0;
        let mut file = 0;
        while rank < 8 && file < 8 && board[rank][file] != target_piece {
            rank += 1;
            if rank == 8 {
                rank = 0;
                file += 1;
            }
        }

        Self::under_attack_by(
            board,
            if color==White { Black } else { White },
            Square { rank: rank as u8, file: file as u8},
        )
    }

    fn valid_wpawn(&self, mov: Move) -> bool {
        if mov.from.rank + 1 != mov.to.rank {
            return mov.from.rank == 1 &&
                mov.from.rank + 2 == mov.to.rank &&
                mov.from.file == mov.to.file &&
                self.piece_at(mov.to) == Empty &&
                self.nothing_between(mov);
        }
        if mov.from.file == mov.to.file { return self.piece_at(mov.to) == Empty }

        if mov.from.file + 1 == mov.to.file {
            return self.piece_at(mov.to).color() == Black || mov.from.rank == 4 && {
                if let Some(last_move) = self.history.last() {
                    last_move.from.rank == 6 &&
                        last_move.to.rank == 4 &&
                        mov.from.file + 1 == last_move.to.file &&
                        self.piece_at(last_move.to) == BPawn
                } else {
                    false
                }
            };
        }

        if mov.to.file + 1 == mov.from.file {
            return self.piece_at(mov.to).color() == Black || mov.from.rank == 4 && {
                if let Some(last_move) = self.history.last() {
                    last_move.from.rank == 6 &&
                        last_move.to.rank == 4 &&
                        last_move.to.file + 1 == mov.from.file &&
                        self.piece_at(last_move.to) == BPawn
                } else {
                    false
                }
            };
        }

        false
    }

    fn valid_bpawn(&self, mov: Move) -> bool {
        if mov.to.rank + 1 != mov.from.rank {
            return mov.from.rank == 6 &&
                mov.to.rank + 2 == mov.from.rank &&
                mov.from.file == mov.to.file &&
                self.piece_at(mov.to) == Empty &&
                self.nothing_between(mov);
        }
        if mov.from.file == mov.to.file { return self.piece_at(mov.to) == Empty }

        if mov.from.file + 1 == mov.to.file {
            return self.piece_at(mov.to).color() == White || mov.from.rank == 3 && {
                if let Some(last_move) = self.history.last() {
                    last_move.from.rank == 1 &&
                        last_move.to.rank == 3 &&
                        mov.from.file + 1 == last_move.to.file &&
                        self.piece_at(last_move.to) == WPawn
                } else {
                    false
                }
            };
        }

        if mov.to.file + 1 == mov.from.file {
            return self.piece_at(mov.to).color() == White || mov.from.rank == 3 && {
                if let Some(last_move) = self.history.last() {
                    last_move.from.rank == 1 &&
                        last_move.to.rank == 3 &&
                        last_move.to.file + 1 == mov.from.file &&
                        self.piece_at(last_move.to) == WPawn
                } else {
                    false
                }
            };
        }

        false
    }

    //assuming orth or diag
    fn nothing_between(&self, mov: Move) -> bool {
        let rstep: i8 = if mov.from.rank < mov.to.rank { 1 } else if mov.from.rank == mov.to.rank { 0 } else { -1 };
        let fstep: i8 = if mov.from.file < mov.to.file { 1 } else if mov.from.file == mov.to.file { 0 } else { -1 };

        let mut r = mov.from.rank as i8 + rstep;
        let mut f = mov.from.file as i8 + fstep;

        while r >= 0 && r < 8 && f >= 0 && f < 8 && !(r==mov.to.rank as i8 && f==mov.to.file as i8) {
            if self.piece_at(Square { rank: r as u8, file: f as u8 }) != Empty { return false; }
            r += rstep;
            f += fstep;
        }

        true
    }

    pub fn under_attack_by(board: &[[Piece; 8]; 8], color: Color, sq: Square) -> bool {
        if color == Color::None { return false; }
        let orth_offsets = [[1,0],[0,1],[-1,0],[0,-1]];
        let diag_offsets = [[1,1],[-1,1],[1,-1],[-1,-1]];
        let pawn_offsets = if color==White { [[-1i8,1i8],[-1,-1]] } else { [[1,1],[1,-1]] };
        let knight_offsets = [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]];

        if Board::check_cont_offsets(board, orth_offsets, sq, Rook, color) { return true; }
        if Board::check_cont_offsets(board, diag_offsets, sq, Bishop, color) { return true; }

        if Board::check_sing_offsets(board, pawn_offsets.iter(), sq, Pawn, color) { return true; }
        if Board::check_sing_offsets(board, knight_offsets.iter(), sq, Knight, color) { return true; }

        false
    }

    fn check_cont_offsets(board: &[[Piece; 8]; 8], offsets: [[i8; 2]; 4], sq: Square, this_dir_only: PieceType, color: Color) -> bool {
        for offset in offsets {
            let mut sq = [sq.rank as i8, sq.file as i8];
            sq[0] += offset[0]; sq[1] += offset[1];
            if !on_board(sq) { continue; }
            let mut piece = board[sq[0]as usize][sq[1]as usize];
            let mut can_be_king = true;
            while on_board(sq) &&
                piece == Empty {
                can_be_king = false;
                piece = board[sq[0]as usize][sq[1]as usize];
                sq[0] += offset[0]; sq[1] += offset[1];
            }
            if piece==this_dir_only.of_color(color) ||
                piece==Queen.of_color(color) ||
                (can_be_king && piece==King.of_color(color)) {
                return true;
            }
        }
        false
    }

    fn check_sing_offsets<'a, I>(board: &[[Piece; 8]; 8], offsets: I, sq: Square, this_piece: PieceType, color: Color) -> bool
    where I: Iterator<Item=&'a[i8; 2]>
    {
        for offset in offsets {
            let sq = [sq.rank as i8 + offset[0] , sq.file as i8 + offset[1]];
            if !on_board(sq) { continue; }
            if board[sq[0]as usize][sq[1]as usize]==this_piece.of_color(color) {
                return true;
            }
        }
        false
    }

    // color to move
    pub fn slow_enumerate_moves(&self, color: Color) -> Vec<Move> {
        let mut vec: Vec<Move> = Vec::with_capacity(50);
        for r in 0u8..8u8 {
            for f in 0u8..8u8 {
                if self.piece_at(Square { rank: r, file: f }).color() == color {
                    for r2 in 0u8..8u8 {
                        for f2 in 0u8..8u8 {
                            let mov = Move { from: Square { rank: r, file: f }, to: Square { rank: r2, file: f2 } };
                            if self.check_move(mov, color) {
                                vec.push(mov);
                            }
                        }
                    }
                }
            }
        }
        vec
    }

    pub fn slow_enumerate_captures(&self, color: Color) -> Vec<Move> {
        let mut vec: Vec<Move> = Vec::with_capacity(50);
        for r2 in 0u8..8u8 {
            for f2 in 0u8..8u8 {
                if self.piece_at(Square { rank: r2, file: f2 }).color() == color.switch() {
                    for r in 0u8..8u8 {
                        for f in 0u8..8u8 {
                            let mov = Move { from: Square { rank: r, file: f }, to: Square { rank: r2, file: f2 } };
                            if self.check_move(mov, color) {
                                vec.push(mov);
                            }
                        }
                    }
                }
            }
        }
        vec
    }

    pub fn slow_enumerate_noncaptures(&self, color: Color) -> Vec<Move> {
        let mut vec: Vec<Move> = Vec::with_capacity(50);
        for r2 in 0u8..8u8 {
            for f2 in 0u8..8u8 {
                if !(self.piece_at(Square { rank: r2, file: f2 }).color() == color.switch()) {
                    for r in 0u8..8u8 {
                        for f in 0u8..8u8 {
                            let mov = Move { from: Square { rank: r, file: f }, to: Square { rank: r2, file: f2 } };
                            if self.check_move(mov, color) {
                                vec.push(mov);
                            }
                        }
                    }
                }
            }
        }
        vec
    }

    pub fn as_string(&self) -> String {
        let mut chars: Vec<char> = Vec::with_capacity(81);

        let ranks = self.state.clone();
        let it = ranks.iter().rev();

        let mut rank_label = 8u32;
        for rank in it {
            chars.push(char::from_digit(rank_label, 10).unwrap());
            rank_label -= 1;

            for piece in rank {
                chars.push(piece.char());
            }

            chars.push('\n');
        }
        chars.append(&mut vec![' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' ]);

        return chars.iter().collect();
    }

    pub fn game_over(&self) -> GameOver {
        if self.slow_enumerate_moves(self.active_color).is_empty() {
            if Self::in_check(&self.state, self.active_color) {
                Checkmate(self.active_color)
            } else {
                Draw
            }
        } else {
            NotOver
        }
    }

    pub fn freeze(&self) -> Self {
        let history = if let Some(mov) = self.history.last() {
            vec![*mov] // ep check
        } else {
            vec![]
        };

        Board {
            initial: self.state,
            state: self.state,
            history,
            castle_rights: self.castle_rights,
            active_color: self.active_color,
        }
    }
}

impl Default for Board {
    fn default() -> Self {
        Board {
            initial: [
            [WRook, WKnight, WBishop, WQueen, WKing, WBishop, WKnight, WRook],  // 1
            [WPawn, WPawn, WPawn, WPawn, WPawn, WPawn, WPawn, WPawn],           // 2
            [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 3
            [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 4
            [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 5
            [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 6
            [BPawn, BPawn, BPawn, BPawn, BPawn, BPawn, BPawn, BPawn],           // 7
            [BRook, BKnight, BBishop, BQueen, BKing, BBishop, BKnight, BRook],  // 8
        ],
            state: [
                [WRook, WKnight, WBishop, WQueen, WKing, WBishop, WKnight, WRook],  // 1
                [WPawn, WPawn, WPawn, WPawn, WPawn, WPawn, WPawn, WPawn],           // 2
                [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 3
                [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 4
                [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 5
                [Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty],           // 6
                [BPawn, BPawn, BPawn, BPawn, BPawn, BPawn, BPawn, BPawn],           // 7
                [BRook, BKnight, BBishop, BQueen, BKing, BBishop, BKnight, BRook],  // 8
            ],
            history: vec![],
            castle_rights: WQUEENSIDE | WKINGSIDE | BQUEENSIDE | BKINGSIDE,
            active_color: White
        }
    }
}

pub fn parse_move(str: &String) -> Move {
    let vec: Vec<char> = str.chars().collect();
    let from = Square {
        rank: vec[1].to_digit(10).unwrap() as u8 - 1,
        file: vec[0].to_digit(20).unwrap() as u8 - 10,
    };

    let to = Square {
        rank: vec[4].to_digit(10).unwrap() as u8 - 1,
        file: vec[3].to_digit(20).unwrap() as u8 - 10,
    };

    return Move { from, to };
}

pub fn print_all_moves(vec: Vec<Move>) {
    for mov in vec {
        print!("{}-{} ", square_to_string(mov.from), square_to_string(mov.to));
    }
    println!();
}

pub fn square_to_string(sq: Square) -> String {
    let char1 = char::from_digit(sq.rank as u32 + 1, 10).unwrap();
    let char2 = char::from_digit(sq.file as u32 + 10, 20).unwrap();
    [char2, char1].iter().collect()
}

pub fn on_board(rf: [i8; 2]) -> bool {
    rf[0] >= 0 && rf[0] < 8 && rf[1] >= 0 && rf[1] < 8
}