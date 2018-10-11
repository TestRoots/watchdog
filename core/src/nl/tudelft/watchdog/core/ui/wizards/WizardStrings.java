package nl.tudelft.watchdog.core.ui.wizards;

public class WizardStrings {
	public static final String WELCOME = "Welcome to WatchDog!";
	public static final String WIZARD_GUIDE = "This wizard guides you through the setup of WatchDog Plugin.";
	public static final String REGISTER = "Please register, so you can access your personal online report.";
	public static final String OPENSOURCE_PLUGIN = "WatchDog is a free, open-source plugin that tells how you code your software";
	public static final String GATHER_NUMERICAL_DATA = "It measures how you write Java code and tests. We never do anything bad with ";
	public static final String DEVELOPMENT_BEHAVIOR = "Based on your development behavior, you can inspect ";
	public static final String REGISTRATION_CONSENT = "By registering, you consent to us storing your data, perform scientific research, and publish it in a completely anonymized form.";
	public static final String FOR_MORE_INFORMATION = "The data is stored with no expiration limit. For more information, please read our ";
	public static final String CONTACT_US ="If you want to revoke your data or contact us for any other manner, please send us an e-mail to \"info AT testroots.org\"";

	public static final String VERIFICATION_BUTTON_TEXT = "Verify";
	public static final String VERIFICATION_SUCCESSFUL_MESSAGE = "ID verification successful!";
	public static final String VERIFICATION_MESSAGE_FAILURE = "ID verification failed.";
	public static final String INPUT_IS_OPTIONAL = "The input is optional, but greatly appreciated to improve the quality of our research data.";
	public static final String INPUT_IS_REQUIRED = "Moving the slider between test and production time is required. All other input is optional.";


	public static final String CONNECTED_TO_INTERNET = "Are you connected to the internet, and is port 80 open?";
	public static final String PLEASE_CONTACT_US = "Please contact us via ";
	public static final String HELP_TROUBLESHOOT = "We can help troubleshoot the issue!";

	public enum Links {
		NUMERICAL_DATA("your purely numerical data", "http://www.testroots.org/testroots_watchdog.html#details"),
        DETAILED_REPORT("a detailed report", "http://www.testroots.org/reports/sample_watchdog_report.pdf"),
        PRIVACY_STATEMENT("privacy statement", "https://testroots.org/testroots_watchdog.html#privacy"),
        WATCHDOG_WEBSITE("our WatchDog website", "https://testroots.org/testroots_watchdog.html"),
        OUR_WEBSITE("our website", "https://www.testroots.org");

        public final String text;
        public final String url;

        Links(String text, String url) {
            this.text = text;
            this.url = url;
        }

        @SuppressWarnings("unused")
        public String toHTMLURL() {
        	return "<a href=\"" + this.url + "\">" + this.text + "</a>";
        }
    }
}
