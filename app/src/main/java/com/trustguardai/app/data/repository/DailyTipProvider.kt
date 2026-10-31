package com.trustguardai.app.data.repository

import java.util.Calendar

data class SecurityTip(
    val title: String,
    val description: String
)

object DailyTipProvider {
    private val tips = listOf(
        SecurityTip("Think Before You Click", "Always verify unexpected links before opening them, especially links requesting passwords or sensitive information."),
        SecurityTip("Use Strong Passwords", "Create unique, complex passwords for every account and avoid using easily guessable information."),
        SecurityTip("Enable Two-Factor Authentication", "Add an extra layer of security by requiring a second form of identification to access your accounts."),
        SecurityTip("Keep Software Updated", "Regularly update your operating system and applications to protect against known security vulnerabilities."),
        SecurityTip("Backup Your Data", "Regularly backup important files to an external drive or secure cloud storage to prevent data loss."),
        SecurityTip("Lock Your Device", "Always lock your computer or mobile device when you step away, even for a short time."),
        SecurityTip("Be Wary of Public Wi-Fi", "Avoid accessing sensitive information or making financial transactions while connected to unsecured public networks."),
        SecurityTip("Secure Your Home Network", "Change the default password on your router and use WPA3 encryption for your home Wi-Fi."),
        SecurityTip("Review App Permissions", "Periodically check the permissions granted to apps on your mobile device and revoke unnecessary access."),
        SecurityTip("Beware of Social Engineering", "Be cautious of phone calls or emails that create a sense of urgency or ask for confidential information."),
        SecurityTip("Protect Your Identity", "Be mindful of the personal information you share on social media and other public platforms."),
        SecurityTip("Check for HTTPS", "Ensure websites use HTTPS before entering any personal or financial information."),
        SecurityTip("Use a Password Manager", "Store and manage your complex passwords securely using a reputable password manager."),
        SecurityTip("Dispose of Hardware Safely", "Wipe all data from old computers, phones, and drives before recycling or selling them."),
        SecurityTip("Stay Alert for Phishing", "Look for subtle signs of phishing, such as misspelled URLs or suspicious sender addresses."),
        SecurityTip("Monitor Your Accounts", "Regularly review your bank and credit card statements for any unauthorized transactions."),
        SecurityTip("Secure Your Mobile Device", "Use biometric authentication or a strong PIN to protect your smartphone or tablet."),
        SecurityTip("Educate Your Team", "Share security best practices with colleagues to foster a culture of cybersecurity awareness."),
        SecurityTip("Report Suspicious Activity", "Immediately notify your security team if you notice anything unusual on your device or network."),
        SecurityTip("Limit Admin Privileges", "Only use administrator accounts when necessary and perform daily tasks with standard user accounts."),
        SecurityTip("Beware of Malicious Downloads", "Only download files and applications from trusted, official sources."),
        SecurityTip("Encrypt Sensitive Files", "Use encryption to protect highly confidential documents stored on your device."),
        SecurityTip("Understand Your Data", "Know what sensitive data you handle and ensure it is stored according to organizational policies."),
        SecurityTip("Use a VPN", "Use a Virtual Private Network to encrypt your internet traffic when working remotely."),
        SecurityTip("Be Careful with USB Drives", "Never plug in unknown USB drives, as they may contain malicious software."),
        SecurityTip("Check Your Digital Footprint", "Search for your name online periodically to see what personal information is publicly available."),
        SecurityTip("Secure Your Email", "Use email filters and be cautious of attachments, even from known senders."),
        SecurityTip("Verify Requests for Money", "Always call to confirm any requests for wire transfers or financial payments, even if they seem to come from executives."),
        SecurityTip("Understand Risk Levels", "Familiarize yourself with security severity levels and the appropriate response actions."),
        SecurityTip("Privacy is Security", "Protecting your privacy is a key part of maintaining overall digital security.")
    )

    fun getDailyTip(): SecurityTip {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        // Deterministic selection based on day and year
        val index = (dayOfYear + year) % tips.size
        return tips[index]
    }
}
