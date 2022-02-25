package codedriver.framework.dashboard.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class DashboardWidgetShowConfigFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IDashboardWidgetShowConfig> chartMap = new HashMap<>();

    public static IDashboardWidgetShowConfig getChart(String chartType, String module, String chartDataSourceName) {
        return chartMap.get(chartType + "_" + module + "_" + chartDataSourceName);
    }

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IDashboardWidgetShowConfig> myMap = context.getBeansOfType(IDashboardWidgetShowConfig.class);
        for (Map.Entry<String, IDashboardWidgetShowConfig> entry : myMap.entrySet()) {
            IDashboardWidgetShowConfig chartCustom = entry.getValue();
            String[] chartTypes = chartCustom.getSupportChart();
            for (String chartType : chartTypes) {
                chartMap.put(chartType + "_" + chartCustom.getModule() + "_" + chartCustom.getName(), chartCustom);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
