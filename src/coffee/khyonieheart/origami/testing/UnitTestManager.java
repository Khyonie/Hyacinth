package coffee.khyonieheart.origami.testing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import coffee.khyonieheart.origami.Logger;
import coffee.khyonieheart.origami.print.Grammar;
import coffee.khyonieheart.origami.util.marker.NotEmpty;

public class UnitTestManager 
{
    public static void performUnitTests(
        boolean printErrors,
        @NotEmpty UnitTestable... unitTests
    ) {
        Logger.log("");
        Logger.log("§6######## PERFORMING " + unitTests.length + " UNIT " + Grammar.plural(unitTests.length, "TEST", "TESTS") + " ########");
        
        List<UnitTestResult> failedTests = new ArrayList<>();
        List<UnitTestResult> allTests = new ArrayList<>();

        int index = -1;
        for (UnitTestable testable : unitTests)
        {
            index++;
            if (testable == null)
            {
                Logger.log("§cUnit test invokation supplied null instance at index " + index + "! Skipping");
                continue;
            }

            Logger.log("§d[" + (index + 1) + "/" + unitTests.length + "] Testing " + testable.getClass().getSimpleName());

            try {
                List<UnitTestResult> results = testable.test();
                Objects.requireNonNull(results);

                allTests.addAll(results);

                for (UnitTestResult result : results)
                {
                    if (result.pass())
                    {
                        continue;
                    }

                    failedTests.add(result);
                }
            } catch (Throwable e) {
                UnitTestResult tempResult = new UnitTestResult(false, "<Exception thrown>", "Failed with throwable " + e.getClass().getSimpleName(), testable);
                failedTests.add(tempResult);
                allTests.add(tempResult);

                if (printErrors)
                {
                    e.printStackTrace();
                }

                continue;
            }
        }

        Logger.log("");
        Logger.log("§6######## UNIT TEST COMPLETE ########");
        Logger.log("Tests ran: " + allTests.size() + " across " + unitTests.length + Grammar.plural(unitTests.length, " class", " classes"));
        Logger.log("Status: " + (failedTests.isEmpty() ? "§aPassed" : "§cFailed"));
        Logger.log("Passes: " + (allTests.size() - failedTests.size()));
        Logger.log("Failures: " + failedTests.size());

        String prefix;
        index = 0;
        for (UnitTestResult result : allTests)
        {
            index++;
            prefix = result.pass() ? "§a" : "§c";

            Logger.log(prefix + "[Result " + index + "/" + allTests.size() + "] Test class: " + result.testable().getClass().getName());
            Logger.log(prefix + " - Description: " + (result.description() != null ? result.description() : "<No description provided>"));
            try {
                Method testMethod = result.testable().getClass().getMethod("test");
                Logger.log(prefix + " - Test identifier: " + (testMethod.isAnnotationPresent(TestIdentifier.class) ? testMethod.getAnnotation(TestIdentifier.class).value() : "<No identifier given>"));
            } catch (NoSuchMethodException e) {}

            if (!result.pass())
            {
                Logger.log(prefix + " - Fail reason: " + (result.failureReason() != null ? result.failureReason() : "<No reason given>"));
            }
        }
    }
}
