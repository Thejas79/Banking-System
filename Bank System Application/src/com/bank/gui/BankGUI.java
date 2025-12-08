package com.bank.gui;

import com.bank.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class BankGUI extends JFrame {
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Color scheme - Professional bank colors
    private static final Color PRIMARY_BLUE = new Color(0, 51, 102);
    private static final Color SECONDARY_BLUE = new Color(0, 102, 153);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(51, 51, 51);
    private static final Color SUCCESS_GREEN = new Color(0, 128, 0);
    private static final Color ERROR_RED = new Color(178, 34, 34);

    public BankGUI() {
        setTitle("SecureBank - Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel header = createHeader("SecureBank", "Welcome to Secure Banking");
        panel.add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(LIGHT_GRAY);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        JLabel titleLabel = new JLabel("Account Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = createTextField("Username");
        JPasswordField passwordField = createPasswordField("Password");

        JButton loginBtn = createPrimaryButton("Sign In");
        JButton registerLink = createLinkButton("New customer? Register here");

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty() || username.equals("Username")
                    || password.equals("Password")) {
                messageLabel.setText("Please enter username and password");
                messageLabel.setForeground(ERROR_RED);
                return;
            }

            try {
                String storedPassword = BankLoginModel.getPasswordOf(username);
                if (storedPassword != null && storedPassword.equals(password)) {
                    currentUser = BankLoginModel.collectUserData(username, password);
                    BankUtil.insertIntoLogTable(new Log(username, ActivityType.LOGIN, null));
                    messageLabel.setText("Login successful!");
                    messageLabel.setForeground(SUCCESS_GREEN);

                    usernameField.setText("");
                    passwordField.setText("");

                    mainPanel.add(createDashboardPanel(), "DASHBOARD");
                    cardLayout.show(mainPanel, "DASHBOARD");
                } else {
                    messageLabel.setText("Invalid username or password");
                    messageLabel.setForeground(ERROR_RED);
                }
            } catch (SQLException ex) {
                messageLabel.setText("Connection error. Please try again.");
                messageLabel.setForeground(ERROR_RED);
            }
        });

        registerLink.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(messageLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerLink);

        centerPanel.add(formPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(createFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel header = createHeader("SecureBank", "Create Your Account");
        panel.add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(LIGHT_GRAY);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)));

        JLabel titleLabel = new JLabel("New Account Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = createTextField("Username");
        JPasswordField passwordField = createPasswordField("Password");
        JTextField firstNameField = createTextField("First Name");
        JTextField lastNameField = createTextField("Last Name");
        JTextField phoneField = createTextField("Phone Number (11 digits)");
        JTextField addressField = createTextField("Address");
        JTextField emailField = createTextField("Email Address");

        JButton registerBtn = createPrimaryButton("Create Account");
        JButton loginLink = createLinkButton("Already have an account? Sign in");

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty() ||
                    username.equals("Username") || firstName.equals("First Name")) {
                messageLabel.setText("Please fill in all fields");
                messageLabel.setForeground(ERROR_RED);
                return;
            }

            try {
                if (!BankRegistrationModel.checkUniqueUsername(username)) {
                    messageLabel.setText("Username already exists");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                if (!BankRegistrationModel.checkUniqueEmail(email)) {
                    messageLabel.setText("Email already registered");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                User newUser = new User(username, password, firstName, lastName, phone, address, email);
                BankRegistrationModel.saveNewUser(newUser);

                messageLabel.setText("Registration successful! Please login.");
                messageLabel.setForeground(SUCCESS_GREEN);

                Timer timer = new Timer(2000, evt -> cardLayout.show(mainPanel, "LOGIN"));
                timer.setRepeats(false);
                timer.start();

            } catch (SQLException ex) {
                messageLabel.setText("Registration failed. Please try again.");
                messageLabel.setForeground(ERROR_RED);
            }
        });

        loginLink.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(firstNameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lastNameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(phoneField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(addressField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(messageLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(loginLink);

        centerPanel.add(formPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(createFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setPreferredSize(new Dimension(900, 80));
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel bankName = new JLabel("SecureBank");
        bankName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        bankName.setForeground(WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            try {
                BankUtil.insertIntoLogTable(new Log(currentUser.getUsername(), ActivityType.LOGOUT, null));
            } catch (SQLException ex) {
            }
            currentUser = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        userPanel.add(welcomeLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(logoutBtn);

        header.add(bankName, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Sidebar and content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 58, 64));
        sidebar.setPreferredSize(new Dimension(200, 500));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        CardLayout dashboardCardLayout = new CardLayout();
        JPanel dashboardContent = new JPanel(dashboardCardLayout);
        dashboardContent.setBackground(LIGHT_GRAY);

        String[] menuItems = { "My Accounts", "Open Account", "Deposit", "Withdraw", "Transfer", "Transactions",
                "Profile" };
        String[] cardNames = { "ACCOUNTS", "OPEN_ACCOUNT", "DEPOSIT", "WITHDRAW", "TRANSFER", "TRANSACTIONS",
                "PROFILE" };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuBtn = createMenuButton(menuItems[i]);
            final String cardName = cardNames[i];
            menuBtn.addActionListener(e -> {
                refreshDashboardContent(dashboardContent, dashboardCardLayout);
                dashboardCardLayout.show(dashboardContent, cardName);
            });
            sidebar.add(menuBtn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        refreshDashboardContent(dashboardContent, dashboardCardLayout);

        contentPanel.add(sidebar, BorderLayout.WEST);
        contentPanel.add(dashboardContent, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(createFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private void refreshDashboardContent(JPanel dashboardContent, CardLayout layout) {
        dashboardContent.removeAll();
        dashboardContent.add(createAccountsPanel(), "ACCOUNTS");
        dashboardContent.add(createOpenAccountPanel(), "OPEN_ACCOUNT");
        dashboardContent.add(createDepositPanel(), "DEPOSIT");
        dashboardContent.add(createWithdrawPanel(), "WITHDRAW");
        dashboardContent.add(createTransferPanel(), "TRANSFER");
        dashboardContent.add(createTransactionsPanel(), "TRANSACTIONS");
        dashboardContent.add(createProfilePanel(), "PROFILE");
        dashboardContent.revalidate();
        dashboardContent.repaint();
    }

    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("My Bank Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        JPanel accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setBackground(LIGHT_GRAY);
        accountsContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        try {
            List<UserBankAccount> accounts = BankViewAccountModel.collectUserBankAccounts(currentUser);

            if (accounts.isEmpty()) {
                JLabel noAccounts = new JLabel("You don't have any bank accounts yet. Open one from the menu.");
                noAccounts.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noAccounts.setForeground(TEXT_DARK);
                accountsContainer.add(noAccounts);
            } else {
                for (UserBankAccount account : accounts) {
                    accountsContainer.add(createAccountCard(account));
                    accountsContainer.add(Box.createVerticalStrut(15));
                }
            }
        } catch (SQLException e) {
            JLabel error = new JLabel("Error loading accounts");
            error.setForeground(ERROR_RED);
            accountsContainer.add(error);
        }

        JScrollPane scrollPane = new JScrollPane(accountsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(LIGHT_GRAY);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccountCard(UserBankAccount account) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        card.setMaximumSize(new Dimension(650, 180));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel accountType = new JLabel(account.getType().name + " Account");
        accountType.setFont(new Font("Segoe UI", Font.BOLD, 16));
        accountType.setForeground(PRIMARY_BLUE);

        JLabel accountId = new JLabel("Account ID: " + account.getBankAccountID());
        accountId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountId.setForeground(new Color(128, 128, 128));

        JLabel status = new JLabel("Status: " + account.getStatus().toString());
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (account.getStatus() == BankAccountStatus.ACTIVE) {
            status.setForeground(SUCCESS_GREEN);
        } else if (account.getStatus() == BankAccountStatus.CLOSED) {
            status.setForeground(ERROR_RED);
        } else {
            status.setForeground(new Color(255, 165, 0));
        }

        // Add action button for pending/active accounts
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        buttonPanel.setOpaque(false);

        if (account.getStatus() == BankAccountStatus.PENDING) {
            JButton activateBtn = new JButton("Activate Account");
            activateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            activateBtn.setBackground(SUCCESS_GREEN);
            activateBtn.setForeground(WHITE);
            activateBtn.setFocusPainted(false);
            activateBtn.setBorderPainted(false);
            activateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            activateBtn.addActionListener(e -> {
                try {
                    BankCloseAccountModel.activateAccount(account);
                    JOptionPane.showMessageDialog(this, "Account activated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to activate account.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(activateBtn);
        } else if (account.getStatus() == BankAccountStatus.ACTIVE) {
            JButton closeBtn = new JButton("Close Account");
            closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            closeBtn.setBackground(ERROR_RED);
            closeBtn.setForeground(WHITE);
            closeBtn.setFocusPainted(false);
            closeBtn.setBorderPainted(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to close this account?",
                        "Confirm Close", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        BankCloseAccountModel.closeAccount(account);
                        JOptionPane.showMessageDialog(this, "Account closed successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Failed to close account.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(closeBtn);
        }

        leftPanel.add(accountType);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(accountId);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(status);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(buttonPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel balanceLabel = new JLabel("Balance");
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        balanceLabel.setForeground(new Color(128, 128, 128));
        balanceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel balance = new JLabel(account.getCurrency().getSymbol() + " " + account.getBalance());
        balance.setFont(new Font("Segoe UI", Font.BOLD, 24));
        balance.setForeground(PRIMARY_BLUE);
        balance.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel withdrawLimit = new JLabel("Withdrawals left: " + account.getWithdrawalLimit());
        withdrawLimit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        withdrawLimit.setForeground(new Color(128, 128, 128));
        withdrawLimit.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(balanceLabel);
        rightPanel.add(balance);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(withdrawLimit);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createOpenAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Open New Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formPanel.setMaximumSize(new Dimension(500, 400));

        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[] { "Basic Account", "Saving Account" });
        accountTypeCombo.setMaximumSize(new Dimension(300, 35));

        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> currencyCombo = new JComboBox<>(
                new String[] { "Dollar ($)", "Euro (€)", "Japanese Yen (¥)", "British Pound (£)" });
        currencyCombo.setMaximumSize(new Dimension(300, 35));

        JLabel balanceLabel = new JLabel("Initial Deposit:");
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField balanceField = new JTextField(20);
        balanceField.setMaximumSize(new Dimension(300, 35));
        balanceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel infoLabel = new JLabel(
                "<html><b>Note:</b> Basic accounts require minimum $0. Saving accounts require minimum $500.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(128, 128, 128));

        JButton openBtn = createPrimaryButton("Open Account");
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        openBtn.addActionListener(e -> {
            try {
                int balance = Integer.parseInt(balanceField.getText().trim());
                int typeIndex = accountTypeCombo.getSelectedIndex();
                int currencyIndex = currencyCombo.getSelectedIndex();

                BankAccountType type = BankAccountType.values()[typeIndex];
                Currency currency = Currency.values()[currencyIndex];

                if (!type.checkValidBalance(balance)) {
                    messageLabel.setText("Insufficient initial deposit for " + type.name + " account");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                UserBankAccount newAccount = new UserBankAccount(
                        currentUser.getUsername(), null, type, currency, null, balance, null, null);
                BankOpenAccountModel.saveNewAccount(currentUser, newAccount);

                messageLabel.setText("Account opened successfully! Status: Pending approval.");
                messageLabel.setForeground(SUCCESS_GREEN);
                balanceField.setText("");

            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid amount");
                messageLabel.setForeground(ERROR_RED);
            } catch (SQLException ex) {
                messageLabel.setText("Error opening account. Please try again.");
                messageLabel.setForeground(ERROR_RED);
            }
        });

        formPanel.add(accountTypeLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(accountTypeCombo);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(currencyLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(currencyCombo);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(balanceLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(balanceField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(infoLabel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(openBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(messageLabel);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setBackground(LIGHT_GRAY);
        centerWrapper.add(formPanel);

        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDepositPanel() {
        return createTransactionPanel("Deposit Funds", true);
    }

    private JPanel createWithdrawPanel() {
        return createTransactionPanel("Withdraw Funds", false);
    }

    private JPanel createTransactionPanel(String titleText, boolean isDeposit) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formPanel.setMaximumSize(new Dimension(500, 350));

        JLabel accountLabel = new JLabel("Select Account:");
        accountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JComboBox<String> accountCombo = new JComboBox<>();
        accountCombo.setMaximumSize(new Dimension(350, 35));

        List<UserBankAccount> accounts = null;
        try {
            accounts = BankViewAccountModel.collectUserBankAccounts(currentUser);
            for (UserBankAccount acc : accounts) {
                if (acc.getStatus() == BankAccountStatus.ACTIVE) {
                    accountCombo.addItem("ID: " + acc.getBankAccountID() + " - " + acc.getType().name +
                            " (" + acc.getCurrency().getSymbol() + acc.getBalance() + ")");
                }
            }
        } catch (SQLException e) {
        }

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField amountField = new JTextField(20);
        amountField.setMaximumSize(new Dimension(350, 35));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton actionBtn = createPrimaryButton(isDeposit ? "Deposit" : "Withdraw");
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final List<UserBankAccount> finalAccounts = accounts;
        actionBtn.addActionListener(e -> {
            try {
                if (accountCombo.getSelectedIndex() < 0) {
                    messageLabel.setText("Please select an account");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                int amount = Integer.parseInt(amountField.getText().trim());
                if (amount <= 0) {
                    messageLabel.setText("Please enter a valid amount");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                int activeIndex = 0;
                UserBankAccount selectedAccount = null;
                for (UserBankAccount acc : finalAccounts) {
                    if (acc.getStatus() == BankAccountStatus.ACTIVE) {
                        if (activeIndex == accountCombo.getSelectedIndex()) {
                            selectedAccount = acc;
                            break;
                        }
                        activeIndex++;
                    }
                }

                if (selectedAccount == null) {
                    messageLabel.setText("Account not found");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                if (isDeposit) {
                    BankDepositModel.deposit(amount, selectedAccount);
                    messageLabel
                            .setText("Successfully deposited " + selectedAccount.getCurrency().getSymbol() + amount);
                    messageLabel.setForeground(SUCCESS_GREEN);
                } else {
                    if (amount > selectedAccount.getBalance()) {
                        messageLabel.setText("Insufficient funds");
                        messageLabel.setForeground(ERROR_RED);
                        return;
                    }
                    if (selectedAccount.getWithdrawalLimit() <= 0) {
                        messageLabel.setText("Monthly withdrawal limit reached");
                        messageLabel.setForeground(ERROR_RED);
                        return;
                    }
                    BankWithdrawalModel.withdraw(amount, selectedAccount);
                    messageLabel.setText("Successfully withdrew " + selectedAccount.getCurrency().getSymbol() + amount);
                    messageLabel.setForeground(SUCCESS_GREEN);
                }
                amountField.setText("");

            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number");
                messageLabel.setForeground(ERROR_RED);
            } catch (SQLException ex) {
                messageLabel.setText("Transaction failed. Please try again.");
                messageLabel.setForeground(ERROR_RED);
            }
        });

        formPanel.add(accountLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(accountCombo);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(actionBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(messageLabel);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setBackground(LIGHT_GRAY);
        centerWrapper.add(formPanel);

        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Transfer Funds");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formPanel.setMaximumSize(new Dimension(500, 400));

        JLabel fromLabel = new JLabel("From Account:");
        fromLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JComboBox<String> fromCombo = new JComboBox<>();
        fromCombo.setMaximumSize(new Dimension(350, 35));

        List<UserBankAccount> accounts = null;
        try {
            accounts = BankViewAccountModel.collectUserBankAccounts(currentUser);
            for (UserBankAccount acc : accounts) {
                if (acc.getStatus() == BankAccountStatus.ACTIVE) {
                    fromCombo.addItem("ID: " + acc.getBankAccountID() + " - " + acc.getType().name +
                            " (" + acc.getCurrency().getSymbol() + acc.getBalance() + ")");
                }
            }
        } catch (SQLException e) {
        }

        JLabel toLabel = new JLabel("To Account ID:");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField toField = new JTextField(20);
        toField.setMaximumSize(new Dimension(350, 35));
        toField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField amountField = new JTextField(20);
        amountField.setMaximumSize(new Dimension(350, 35));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton transferBtn = createPrimaryButton("Transfer");
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final List<UserBankAccount> finalAccounts = accounts;
        transferBtn.addActionListener(e -> {
            try {
                if (fromCombo.getSelectedIndex() < 0) {
                    messageLabel.setText("Please select source account");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                String toAccountId = toField.getText().trim();
                int amount = Integer.parseInt(amountField.getText().trim());

                if (toAccountId.isEmpty()) {
                    messageLabel.setText("Please enter recipient account ID");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                if (amount <= 0) {
                    messageLabel.setText("Please enter a valid amount");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                int activeIndex = 0;
                UserBankAccount selectedAccount = null;
                for (UserBankAccount acc : finalAccounts) {
                    if (acc.getStatus() == BankAccountStatus.ACTIVE) {
                        if (activeIndex == fromCombo.getSelectedIndex()) {
                            selectedAccount = acc;
                            break;
                        }
                        activeIndex++;
                    }
                }

                if (selectedAccount == null) {
                    messageLabel.setText("Source account not found");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                if (amount > selectedAccount.getBalance()) {
                    messageLabel.setText("Insufficient funds");
                    messageLabel.setForeground(ERROR_RED);
                    return;
                }

                BankWithdrawalModel.withdraw(amount, selectedAccount);
                BankDepositModel.depositMoneyToUserBankAccount(amount,
                        new UserBankAccount(null, Integer.parseInt(toAccountId), null, null, null, 0, null, null));

                messageLabel.setText("Successfully transferred " + selectedAccount.getCurrency().getSymbol() + amount);
                messageLabel.setForeground(SUCCESS_GREEN);
                toField.setText("");
                amountField.setText("");

            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter valid numbers");
                messageLabel.setForeground(ERROR_RED);
            } catch (SQLException ex) {
                messageLabel.setText("Transfer failed. Check recipient account ID.");
                messageLabel.setForeground(ERROR_RED);
            }
        });

        formPanel.add(fromLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(fromCombo);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(toLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(toField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(amountLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(transferBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(messageLabel);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setBackground(LIGHT_GRAY);
        centerWrapper.add(formPanel);

        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "Type", "Amount", "Date" };
        Object[][] data = new Object[0][3];

        try {
            List<Transaction> transactions = BankTransactionsModel.collectTransactions(currentUser.getUsername(), 50,
                    0);
            data = new Object[transactions.size()][3];
            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                data[i][0] = t.getType().toString();
                data[i][1] = "$" + t.getAmount();
                data[i][2] = t.getDate();
            }
        } catch (SQLException e) {
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(WHITE);
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.getViewport().setBackground(WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(LIGHT_GRAY);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_BLUE);
        panel.add(title, BorderLayout.NORTH);

        JPanel profileCard = new JPanel();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBackground(WHITE);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        profileCard.setMaximumSize(new Dimension(500, 350));

        String[][] profileData = {
                { "Username", currentUser.getUsername() },
                { "Name", currentUser.getFirstName() + " " + currentUser.getLastName() },
                { "Email", currentUser.getEmail() },
                { "Phone", currentUser.getPhoneNumber() },
                { "Address", currentUser.getAddress() }
        };

        for (String[] rowData : profileData) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(400, 40));

            JLabel label = new JLabel(rowData[0] + ":");
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(new Color(100, 100, 100));
            label.setPreferredSize(new Dimension(100, 30));

            JLabel value = new JLabel(rowData[1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            value.setForeground(TEXT_DARK);

            row.add(label, BorderLayout.WEST);
            row.add(value, BorderLayout.CENTER);

            profileCard.add(row);
            profileCard.add(Box.createVerticalStrut(10));
        }

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerWrapper.setBackground(LIGHT_GRAY);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        centerWrapper.add(profileCard);

        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeader(String titleText, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PRIMARY_BLUE);
        header.setPreferredSize(new Dimension(900, 100));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitleLabel);

        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(52, 58, 64));
        footer.setPreferredSize(new Dimension(900, 40));

        JLabel copyright = new JLabel("© 2024 SecureBank. All rights reserved. | FDIC Insured");
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyright.setForeground(new Color(150, 150, 150));

        footer.add(copyright);

        return footer;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        field.setMaximumSize(new Dimension(300, 40));
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        field.setMaximumSize(new Dimension(300, 40));
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(TEXT_DARK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(300, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_BLUE);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_BLUE);
            }
        });

        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton("<html><u>" + text + "</u></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(SECONDARY_BLUE);
        button.setBackground(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(WHITE);
        button.setBackground(new Color(52, 58, 64));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(73, 80, 87));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 58, 64));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (BankUtil.connection == null) {
                    JOptionPane.showMessageDialog(null,
                            "Cannot connect to database. Please ensure MySQL is running.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }

                BankGUI app = new BankGUI();
                app.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
