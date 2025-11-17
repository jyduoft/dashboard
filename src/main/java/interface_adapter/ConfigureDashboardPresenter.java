package interface_adapter;

import entity.DashboardConfig;
import use_cases.ConfigureDashboardOutputBoundary;
import use_cases.ConfigureDashboardResponseModel;

public class ConfigureDashboardPresenter implements ConfigureDashboardOutputBoundary {

    private final DashboardViewModel viewModel;

    public ConfigureDashboardPresenter(DashboardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(ConfigureDashboardResponseModel responseModel) {
        DashboardConfig config = responseModel.getConfig();
        viewModel.setConfig(config);
    }
}
