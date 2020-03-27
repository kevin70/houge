package io.zhudy.xim;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * 集成测试环境.
 *
 * <p>默认将 {@code Env} 设置为 {@link Env#INTEGRATION_TEST}.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class XimTestExecutionListener implements TestExecutionListener {

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    System.setProperty("xim.env", "INTEGRATION_TEST");
  }
}
