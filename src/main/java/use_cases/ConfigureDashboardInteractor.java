package use_cases;

import entity.DashboardConfig;

public class ConfigureDashboardInteractor implements ConfigureDashboardInputBoundary {

    private final DashboardConfigDataAccessInterface configGateway;
    private final ConfigureDashboardOutputBoundary presenter;

    public ConfigureDashboardInteractor(DashboardConfigDataAccessInterface configGateway,
                                        ConfigureDashboardOutputBoundary presenter) {
        this.configGateway = configGateway;
        this.presenter = presenter;
    }

    @Override
    public void configure(ConfigureDashboardRequestModel requestModel) {
        DashboardConfig config = new DashboardConfig(
                requestModel.isShowTasks(),
                requestModel.isShowStocks(),
                requestModel.isShowWeather(),
                requestModel.isShowMap(),
                requestModel.isShowPokemon()
        );

        configGateway.save(config);

        ConfigureDashboardResponseModel response =
                new ConfigureDashboardResponseModel(config);
        presenter.present(response);
    }
}
