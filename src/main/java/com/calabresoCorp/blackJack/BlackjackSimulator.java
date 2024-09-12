package com.calabresoCorp.blackJack;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BlackjackSimulator {
	static final int[] cardValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	static final int DECK_SIZE = 52;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String continuePlaying = "y";

		while (continuePlaying.equalsIgnoreCase("y")) {
			System.out.println("Digite as cartas recebidas pelo jogador (ex: 1,10 para Ás e Rei): ");
			int[] playerCards = parseInput(scanner.nextLine());

			System.out.println("Digite as cartas recebidas pela casa (ex: 5,10 para 5 e Rei): ");
			int[] dealerCards = parseInput(scanner.nextLine());

			calculateProbabilities(playerCards, dealerCards);

			System.out.println("Deseja jogar novamente? (y/n): ");
			continuePlaying = scanner.nextLine().trim();
		}

		System.out.println("Obrigado por jogar!");
		scanner.close();
	}

	public static void calculateProbabilities(int[] playerCards, int[] dealerCards) {
		int numSimulations = 1000000;
		Map<String, Integer> outcomes = new HashMap<>();

		for (int i = 0; i < numSimulations; i++) {
			String result = simulateRound(playerCards, dealerCards);
			outcomes.put(result, outcomes.getOrDefault(result, 0) + 1);
		}

		printProbabilities(outcomes, numSimulations);
	}

	public static String simulateRound(int[] playerCards, int[] dealerCards) {
		int playerScore = calculateInitialScore(playerCards);
		int dealerScore = calculateInitialScore(dealerCards);

		playerScore = playHand(playerScore);
		dealerScore = playDealerHand(dealerScore);

		if (playerScore > 21) return "Perda";
		if (dealerScore > 21) return "Vitória";
		if (playerScore > dealerScore) return "Vitória";
		if (playerScore < dealerScore) return "Perda";
		return "Empate";
	}

	public static int calculateInitialScore(int[] cards) {
		int score = 0;
		boolean hasAce = false;

		for (int card : cards) {
			score += card;
			if (card == 1) hasAce = true;
		}

		if (hasAce && score + 10 <= 21) score += 10;
		return score;
	}

	public static int playHand(int currentScore) {
		boolean hasAce = (currentScore <= 11);
		while (currentScore < 17) {
			int cardValue = drawCard();
			currentScore += cardValue;
			if (cardValue == 1) hasAce = true;

			if (currentScore > 21 && hasAce) {
				currentScore -= 10;
				hasAce = false;
			}
		}
		return currentScore;
	}

	public static int playDealerHand(int currentScore) {
		while (currentScore < 17) {
			int cardValue = drawCard();
			currentScore += cardValue;
			if (cardValue == 1 && currentScore > 21) {
				currentScore -= 10;
			}
		}
		return currentScore;
	}

	public static int drawCard() {
		int index = (int) (Math.random() * 13);
		return cardValues[index];
	}

	public static void printProbabilities(Map<String, Integer> outcomes, int totalRounds) {
		System.out.println("Resultado | Probabilidade (%)");
		for (Map.Entry<String, Integer> entry : outcomes.entrySet()) {
			double probability = (double) entry.getValue() / totalRounds * 100;
			System.out.printf("%9s | %15.2f%n", entry.getKey(), probability);
		}
	}

	public static int[] parseInput(String input) {
		String[] tokens = input.split(",");
		int[] cards = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			cards[i] = Integer.parseInt(tokens[i].trim());
		}
		return cards;
	}
}
