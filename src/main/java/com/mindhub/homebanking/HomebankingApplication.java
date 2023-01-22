package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {
			Client lucia = new Client("Lucia", "Vidal", "lucividal09@gmail.com", passwordEncoder.encode("lucia"));
			Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("lucia"));
			Client dante = new Client("Dante", "Vilches", "dantevilches@gmail.com", passwordEncoder.encode("dante"));
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.00, true, AccountType.CHECKING);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.00, true, AccountType.SAVING);
			Account account3 = new Account("VIN003", LocalDateTime.now(), 10000.00, true, AccountType.CHECKING);
			Account account4 = new Account("VIN004", LocalDateTime.now().plusDays(1), 2000.00, true, AccountType.SAVING);



			Card card1 = new Card(lucia.getFirstName() + " " + lucia.getLastName(), "12-23-421-234", 123, LocalDate.now(),
					LocalDate.now().plusYears(5), CardType.CREDIT, CardColor.GOLD);
			Card card2 = new Card(lucia.getFirstName() + " " + lucia.getLastName(), "1245200000000232301", 174, LocalDate.now(),
					LocalDate.now().plusYears(5), CardType.CREDIT, CardColor.TITANIUM);
			Card card3 = new Card(dante.getFirstName() + " " + dante.getLastName(), "126420000000392301", 231, LocalDate.now(),
					LocalDate.now().plusYears(10), CardType.DEBIT, CardColor.SILVER);


			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 150.00, "pruebaa", LocalDateTime.now().minusMonths(1), 120.00);
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, 150.00, "pruebaa2", LocalDateTime.now(), 1200.00);
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, 1200.00, "pruebaa3", LocalDateTime.now(), 500.00);
			Transaction transaction4 = new Transaction(TransactionType.CREDIT, 1400.00, "pruebaa4", LocalDateTime.now(), 900.00);
			Transaction transaction5 = new Transaction(TransactionType.DEBIT, 14500.00, "pruebaa4", LocalDateTime.now(),231.00);
			Transaction transaction6 = new Transaction(TransactionType.CREDIT, 14003.00, "pruebaa4", LocalDateTime.now(), 912.00);

			account1.addTransaction(transaction1);
			account1.addTransaction(transaction5);
			account1.addTransaction(transaction6);
			account2.addTransaction(transaction2);
			account3.addTransaction(transaction3);
			account4.addTransaction(transaction4);

			lucia.addAccount(account1);
			lucia.addAccount(account2);
			dante.addAccount(account3);
			dante.addAccount(account4);

			List<Integer> payments1 = Arrays.asList(12,24,36,48,60);
			List<Integer> payments2 = Arrays.asList(6,12,24);
			List<Integer> payments3 = Arrays.asList(6,12,24,36);

			Loan loan1 = new Loan("Mortgage", 500000.00, payments1, 35);
			Loan loan2 = new Loan("Personal", 100000.00, payments2, 30);
			Loan loan3= new Loan("Car loan", 300000.00, payments3, 25);

			ClientLoan clientLoan1 = new ClientLoan(400000.00, payments1.get(4), LocalDateTime.now());

			loan1.addClientLoan(clientLoan1);
			lucia.addClientLoan(clientLoan1);

			lucia.setEnabled(true);
			dante.setEnabled(true);
			admin.setEnabled(true);

			lucia.addCard(card1);
			lucia.addCard(card2);
			dante.addCard(card3);


			clientRepository.save(lucia);
			clientRepository.save(dante);
			clientRepository.save(admin);

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			clientLoanRepository.save(clientLoan1);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);



			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);

		};
	}
}
