package codedriver.framework.dashboard.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class DashboardChartCustomFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IDashboardChartCustom> chartMap = new HashMap<>();

    public static IDashboardChartCustom getChart(String chartType, String module) {
        return chartMap.get(chartType + "_" + module);
    }

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IDashboardChartCustom> myMap = context.getBeansOfType(IDashboardChartCustom.class);
        for (Map.Entry<String, IDashboardChartCustom> entry : myMap.entrySet()) {
            IDashboardChartCustom chartCustom = entry.getValue();
            String[] chartTypes = chartCustom.getSupportChart();
            for (String chartType : chartTypes) {
                chartMap.put(chartType + "_" + chartCustom.getModule(), chartCustom);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
