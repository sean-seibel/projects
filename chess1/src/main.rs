use std::io::stdin;
use std::process::exit;
use std::time::{Duration, Instant};
use crate::board::{Board, INIT_BASE, Move, parse_move, print_all_moves};
use crate::piece::Color::{Black, White};
use termion::{clear, cursor};
use crate::heuristics::endgame_reached::in_endgame;
use crate::heuristics::inf_old::influence_grid;
use crate::heuristics::material_count::material_count;
use crate::heuristics::movability_old::{movabilities, movability_coeff};
use crate::heuristics::piece_square_tables::{pst_analysis, pst_board};
use crate::heuristics::sminf_old::{smart_influence_grid, smart_influence_grid_double, sminf_coeff};
use crate::heuristics::tensity::{remove_cheapest_attacker, tension_board, tension_against_color, tensions, total_tension, threat_value};
use crate::piece::{BKINGSIDE, BQUEENSIDE, GameOver, WKINGSIDE, WQUEENSIDE};
use crate::piece::Piece::{BBishop, BPawn, BQueen, WPawn, WQueen, WRook};

mod piece;
mod board;
mod heuristics;
mod suggestions;


fn main() {
    let mut board = Board::default();
    let mut last_input = String::from("");
    let mut eval_on = false;
    let mut time_on = false;
    let mut time = Instant::now();
    let mut last_time = Duration::from_millis(0);
    print!("{}", clear::All);
    print!("{}", cursor::Goto(1,1));
    loop {
        if eval_on {
            let eval = board.analysis_with_imminent_attacks();
            println!("{}{}.{}", if eval < 0 { "-" } else { "" }, eval.abs() / 1000, format!("{:0>3}", eval.abs() % 1000));
        }
        if time_on {
            println!("{:?}", last_time);
        }
        println!("{}", board.as_string());
        let res = board.game_over();
        match res {
            GameOver::NotOver => {}
            GameOver::Checkmate(_) => { println!("Checkmate"); break; }
            GameOver::Draw => { println!("Stalemate"); break; }
        }
        last_input.clear();
        stdin().read_line(&mut last_input).expect("Failed to read from stdin");
        print!("{}", clear::All);
        print!("{}", cursor::Goto(1,1));
        let input = last_input.trim_end();
        if input == "q" { break; }
        if input == "time" {
            time_on = !time_on;
            continue;
        }
        if input == "enum" {
            let cats = board.slow_enumerate_categories(board.active_color);
            print!("checks:"); print_all_moves(&cats.0);
            print!("\ncaptures:"); print_all_moves(&cats.1);
            print!("\nother:"); print_all_moves(&cats.2);
            continue;
        }
        if input == "hope" {
            print_all_moves(&board.slow_enumerate_hopeful_order(board.active_color));
            continue;
        }
        if input == "eval" {
            eval_on = !eval_on;
            continue;
        }
        if input == "hist" {
            print_all_moves(&board.history);
            println!("move_count:{}", board.move_count);
            continue;
        }
        if input == "mat" {
            println!("{}", material_count(&board.state));
            continue;
        }
        if input == "inf" {
            let infs = influence_grid(&board.state);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     infs[7], infs[6], infs[5], infs[4], infs[3], infs[2], infs[1], infs[0]);
            continue;
        }
        if input == "sminf" {
            let infs = smart_influence_grid(&board.state);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     infs[7], infs[6], infs[5], infs[4], infs[3], infs[2], infs[1], infs[0]);
            println!("coeff:{}", sminf_coeff(&board.state, &infs, board.active_color));
            continue;
        }
        if input == "movab" {
            let movabs = movabilities(&board.state);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     movabs[7], movabs[6], movabs[5], movabs[4], movabs[3], movabs[2], movabs[1], movabs[0]);
            println!("total:{}", movability_coeff(&board.state, &movabs, board.move_count));
            continue;
        }
        if input == "pst" {
            let psts = pst_board(&board.state);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     psts[7], psts[6], psts[5], psts[4], psts[3], psts[2], psts[1], psts[0]);
            println!("{}", pst_analysis(&board.state));
            continue;
        }
        if input == "cast" {
            println!("{}", board.castle_rights);
            let wks = board.castle_rights & WKINGSIDE > 0;
            let wqs = board.castle_rights & WQUEENSIDE > 0;
            let bks = board.castle_rights & BKINGSIDE > 0;
            let bqs = board.castle_rights & BQUEENSIDE > 0;
            println!("bks:{}, bqs:{}, wks:{}, wqs:{}", bks, bqs, wks, wqs);
            continue;
        }
        if input == "eg" {
            println!("{}", in_endgame(&board.state));
            continue;
        }
        if input == "ep" {
            println!("{}", board.en_passant);
            continue;
        }
        if input == "undo" {
            board.undo(&INIT_BASE);
            continue;
        }
        if input == "tens" {
            let (winfs, binfs) = smart_influence_grid_double(&board.state);
            let infs = tension_board(&board.state, &winfs, &binfs);
            let tensions = tensions(&board.state, &winfs, &binfs);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     infs[7], infs[6], infs[5], infs[4], infs[3], infs[2], infs[1], infs[0]);
            println!("num attacks:{:?}", tensions);
            println!("coeff:{:?}", tension_against_color(&tensions, board.active_color));
            println!("total:{:?}", total_tension(&tensions));
            continue;
        }
        if input == "threat" {
            let (winfs, binfs) = smart_influence_grid_double(&board.state);
            let infs = tension_board(&board.state, &winfs, &binfs);
            let val = threat_value(&board.state, infs, board.active_color);
            println!("to move: {:?}\nthreat_value:{}",board.active_color, val);
            continue;
        }
        if !check_well_formed_move(input) {
            println!("Invalid move: {}", input);
            continue;
        }
        let mov = if input == "ai" {
            time = Instant::now();
            board.abq_suggest_move()
        } else {
            parse_move(&last_input)
        };
        if board.check_move(mov, board.active_color) {
            board.make_move(mov);
            last_time = time.elapsed();
            time = Instant::now();
        } else {
            println!("Invalid move: {}", input)
        }
    }
}

fn check_well_formed_move(s: &str) -> bool {
    if s == "ai" { return true; }
    if s.len() < 5 { return false; }
    let v: Vec<char> = s.chars().collect();
    a_h(v[0]) && one_eight(v[1]) && a_h(v[3]) && one_eight(v[4])
}

fn a_h(c: char) -> bool {
    match c {
        'a'..='h' => true,
        _ => false,
    }
}
fn one_eight(c: char) -> bool {
    match c {
        '1'..='8' => true,
        _ => false,
    }
}
