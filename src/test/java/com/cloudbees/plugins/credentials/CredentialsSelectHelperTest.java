package com.cloudbees.plugins.credentials;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import hudson.model.UnprotectedRootAction;
import java.util.List;
import org.hamcrest.Matchers;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlListItem;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSpan;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;

public class CredentialsSelectHelperTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void doAddCredentialsFromPopupWorksAsExpected() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage htmlPage = wc.goTo("credentials-selection");
            HtmlButton addCredentialsButton = htmlPage.querySelector(".credentials-add-menu");
            addCredentialsButton.click();
            HtmlListItem li = htmlPage.querySelector(".credentials-add-menu-items li");
            li.click();
            wc.waitForBackgroundJavaScript(4000);
            HtmlForm form = htmlPage.querySelector("#credentialsDialog form");

            HtmlInput username = form.querySelector("input[name='_.username']");
            username.setValue("bob");
            HtmlInput password = form.querySelector("input[name='_.password']");
            password.setValue("secret");
            HtmlInput id = form.querySelector("input[name='_.id']");
            id.setValue("test");

            HtmlSpan formSubmitButton = form.querySelector("#credentials-add-submit");
            formSubmitButton.fireEvent("click");
            wc.waitForBackgroundJavaScript(5000);

            // check if credentials were added
            List<UsernamePasswordCredentials> creds = CredentialsProvider.lookupCredentials(UsernamePasswordCredentials.class);
            assertThat(creds, Matchers.hasSize(1));
            UsernamePasswordCredentials cred = creds.get(0);
            assertThat(cred.getUsername(), is("bob"));
            assertThat(cred.getPassword().getPlainText(), is("secret"));
        }
    }

    @TestExtension
    public static class CredentialsSelectionAction implements UnprotectedRootAction {
        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "credentials-selection";
        }
    }
}
