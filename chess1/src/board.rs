use std::iter::repeat;
use std::thread::sleep;
use std::time::Duration;
use crate::{Black, in_endgame, smart_influence_grid_double, tension_board, White};
use crate::heuristics::tensity::Attack;
use crate::piece::*;
use crate::piece::GameOver::{Checkmate, Draw, NotOver};
use crate::piece::Piece::*;
use crate::piece::PieceType::{Bishop, King, Knight, Pawn, Queen, Rook};


#[derive(Clone)]
pub struct Board {
    pub state: [[Piece; 8]; 8],
    pub history: Vec<Move>,
    pub castle_rights: u8,
    pub active_color: Color,
    pub move_count: usize,
    pub en_passant: u8,
}

pub struct InitialState {
    pub state: [[Piece; 8]; 8],
    pub castle_rights: u8,
    pub active_color: Color,
    pub move_count: usize,
    pub en_passant: u8,
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
        if piece == WPawn && mov.to.rank == 7 {
            return WQueen;
        }
        if piece == BPawn && mov.to.rank == 0 {
            return BQueen;
        }
        if piece == WPawn && target == Empty {
            if mov.to.rank != 0 {
            board[mov.to.rank as usize - 1][mov.to.file as usize] = Empty; }
            return piece;
        }
        if piece == BPawn && target == Empty {
            if mov.to.rank as usize != 7 {
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
        self.make_move_push(mov, true);
    }

    pub fn make_move_push(&mut self, mov: Move, push: bool) {
        match mov.from {
            Square { rank: 0, file: 4 } => self.castle_rights &= !(WQUEENSIDE | WKINGSIDE),
            Square { rank: 7, file: 4 } => self.castle_rights &= !(BQUEENSIDE | BKINGSIDE),
            Square { rank: 0, file: 0 } => self.castle_rights &= !WQUEENSIDE,
            Square { rank: 7, file: 0 } => self.castle_rights &= !BQUEENSIDE,
            Square { rank: 0, file: 7 } => self.castle_rights &= !WKINGSIDE,
            Square { rank: 7, file: 7 } => self.castle_rights &= !BKINGSIDE,
            _ => {}
        }

        if push{ self.history.push(mov); }

        self.en_passant = 8;

        let piece = Board::check_for_side_effects(&mut self.state, &mov);
        self.state[mov.to.rank as usize][mov.to.file as usize] = piece;
        self.state[mov.from.rank as usize][mov.from.file as usize] = Empty;
        if piece == WPawn || piece == BPawn && mov.to.rank.abs_diff(mov.from.rank) == 2 {
            self.en_passant = mov.from.file;
        }

        self.active_color = self.active_color.switch();

        self.move_count += 1;
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
            color.switch(),
            Square { rank: rank as u8, file: file as u8},
        )
    }

    fn valid_wpawn(&self, mov: Move) -> bool {
        if mov.from.rank + 1 != mov.to.rank {
            return mov.from.rank == 1 &&
                mov.from.rank + 2 == mov.to.rank &&
                mov.from.file == mov.to.file &&
                self.piece_at(mov.to) == Empty &&
                self.piece_at(Square { rank: 2, file: mov.to.file }) == Empty;
        }
        if mov.from.file == mov.to.file { return self.piece_at(mov.to) == Empty }

        mov.to.file.abs_diff(mov.from.file) == 1 &&
            (self.piece_at(mov.to).color() == Black ||
                (mov.from.rank == 4 && mov.to.file == self.en_passant))
    }

    fn valid_bpawn(&self, mov: Move) -> bool {
        if mov.to.rank + 1 != mov.from.rank {
            return mov.from.rank == 6 &&
                mov.to.rank + 2 == mov.from.rank &&
                mov.from.file == mov.to.file &&
                self.piece_at(mov.to) == Empty &&
                self.piece_at(Square { rank: 5, file: mov.to.file }) == Empty;
        }
        if mov.from.file == mov.to.file { return self.piece_at(mov.to) == Empty }

        mov.to.file.abs_diff(mov.from.file) == 1 &&
            (self.piece_at(mov.to).color() == White ||
                (mov.from.rank == 3 && mov.to.file == self.en_passant))
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

    pub fn slow_enumerate_categories(&self, color: Color) -> (Vec<Move>, Vec<Move>, Vec<Move>) {
        let mut checks: Vec<Move> = Vec::with_capacity(10);
        let mut captures: Vec<Move> = Vec::with_capacity(20);
        let mut other: Vec<Move> = Vec::with_capacity(50);
        let mut boards = repeat(self.state.clone());
        let other_color = color.switch();
        for r in 0u8..8u8 {
            for f in 0u8..8u8 {
                if self.piece_at(Square { rank: r, file: f }).color() == color {
                    for r2 in 0u8..8u8 {
                        for f2 in 0u8..8u8 {
                            let mov = Move { from: Square { rank: r, file: f }, to: Square { rank: r2, file: f2 } };

                            if self.check_move(mov, color) {
                                let mut board = boards.next().unwrap();
                                let capture = board[mov.to.rank as usize][mov.to.file as usize].color() == other_color;
                                let piece = Board::check_for_side_effects(&mut board, &mov);
                                board[mov.to.rank as usize][mov.to.file as usize] = piece;
                                board[mov.from.rank as usize][mov.from.file as usize] = Empty;
                                if Board::in_check(&board, other_color) {
                                    checks.push(mov);
                                } else if capture {
                                    captures.push(mov);
                                } else {
                                    other.push(mov);
                                }
                            }
                        }
                    }
                }
            }
        }

        (checks, captures, other)
    }

    pub fn slow_enumerate_hopeful_order(&self, color: Color) -> Vec<Move> {
        let (mut checks, mut captures, mut noncaptures) = self.slow_enumerate_categories(color);
        captures.sort_unstable_by( |mov1, mov2| {
            // (self.mat_diff(mov1)).cmp(&(self.mat_diff(mov2)))
            (self.mat_diff(mov2)).cmp(&(self.mat_diff(mov1)))
        });
        let endgame = in_endgame(&self.state);
        noncaptures.sort_unstable_by( |mov1, mov2| {
            // self.happiness_diff(mov1, endgame).cmp(&self.happiness_diff(mov2, endgame))
            self.happiness_diff(mov2, endgame).cmp(&self.happiness_diff(mov1, endgame))
        });
        checks.append(&mut captures);
        checks.append(&mut noncaptures);
        checks
    }

    fn happiness_diff(&self, mov: &Move, endgame: i32) -> i32 {
        self.piece_at(mov.from).happiness_at(mov.to, endgame) -
            self.piece_at(mov.from).happiness_at(mov.from, endgame)
    }

    fn mat_diff(&self, mov: &Move) -> i32 {
        self.piece_at(mov.to).material_eval_base() -
            self.piece_at(mov.from).material_eval_base()
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
                for c in piece.char().chars() {
                    chars.push(c);
                }
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

    pub fn undo(&mut self, initial: &InitialState) {
        self.history.pop();
        self.state = initial.state;
        self.castle_rights = initial.castle_rights;
        self.active_color = initial.active_color;
        self.move_count = initial.move_count;
        self.en_passant = initial.en_passant;

        for mov in self.history.clone() {
            self.make_move_push(mov, false);
        }
    }

    pub fn freeze(&self) -> Self {
        Board {
            state: self.state,
            history: vec![],
            castle_rights: self.castle_rights,
            active_color: self.active_color,
            move_count: self.move_count,
            en_passant: self.en_passant,
        }
    }

    pub fn freeze_init(&self) -> InitialState {
        InitialState {
            state: self.state,
            castle_rights: self.castle_rights,
            active_color: self.active_color,
            move_count: self.move_count,
            en_passant: self.en_passant,
        }
    }
}

impl Default for Board {
    fn default() -> Self {
        Board {
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
            history: Vec::with_capacity(64),
            castle_rights: WQUEENSIDE | WKINGSIDE | BQUEENSIDE | BKINGSIDE,
            active_color: White,
            move_count: 0,
            en_passant: 8,
        }
    }
}

pub const INIT_BASE: InitialState = InitialState {
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
    castle_rights: WQUEENSIDE | WKINGSIDE | BQUEENSIDE | BKINGSIDE,
    active_color: White,
    move_count: 0,
    en_passant: 8,
};

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

pub fn print_all_moves(vec: &Vec<Move>) {
    for mov in vec {
        println!("{}-{}", square_to_string(mov.from), square_to_string(mov.to));
    }
}

pub fn square_to_string(sq: Square) -> String {
    let char1 = char::from_digit(sq.rank as u32 + 1, 10).unwrap();
    let char2 = char::from_digit(sq.file as u32 + 10, 20).unwrap();
    [char2, char1].iter().collect()
}

pub fn on_board(rf: [i8; 2]) -> bool {
    rf[0] >= 0 && rf[0] < 8 && rf[1] >= 0 && rf[1] < 8
}