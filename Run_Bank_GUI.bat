@echo off
title SecureBank - Banking System
cd /d "%~dp0Bank System Application"
java -cp "out;src/lib/mysql-connector-j-9.4.0.jar" com.bank.gui.BankGUI
pause
