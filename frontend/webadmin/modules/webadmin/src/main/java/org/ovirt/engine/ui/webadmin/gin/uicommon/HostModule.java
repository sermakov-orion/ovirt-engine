package org.ovirt.engine.ui.webadmin.gin.uicommon;

import java.util.ArrayList;
import java.util.Map;

import org.ovirt.engine.core.common.businessentities.AuditLog;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.permissions;
import org.ovirt.engine.core.compat.StringHelper;
import org.ovirt.engine.ui.common.presenter.AbstractModelBoundPopupPresenterWidget;
import org.ovirt.engine.ui.common.presenter.ModelBoundPresenterWidget;
import org.ovirt.engine.ui.common.presenter.popup.RemoveConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.common.uicommon.model.DetailModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.DetailTabModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.MainModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.MainTabModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.SearchableDetailModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.SearchableDetailTabModelProvider;
import org.ovirt.engine.ui.uicommonweb.ReportCommand;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.models.ConfirmationModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;
import org.ovirt.engine.ui.uicommonweb.models.configure.PermissionListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostHardwareGeneralModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostBondInterfaceModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostEventListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostGeneralModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostHooksListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostInterfaceLineModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostInterfaceListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostInterfaceModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostManagementNetworkModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostSetupNetworksModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostVmListModel;
import org.ovirt.engine.ui.webadmin.gin.ClientGinjector;
import org.ovirt.engine.ui.webadmin.section.main.presenter.ReportPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.AssignTagsPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.DetachConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.PermissionsPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.ConfigureLocalStoragePopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostBondPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostInstallPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostInterfacePopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostManagementConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostManagementPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostSetupNetworksPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.ManualFencePopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.SetupNetworksBondPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.SetupNetworksInterfacePopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.SetupNetworksManagementPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.vm.VmMigratePopupPresenterWidget;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class HostModule extends AbstractGinModule {

    // Main List Model

    @Provides
    @Singleton
    public MainModelProvider<VDS, HostListModel> getHostListProvider(ClientGinjector ginjector,
            final Provider<HostPopupPresenterWidget> popupProvider,
            final Provider<RemoveConfirmationPopupPresenterWidget> removeConfirmPopupProvider,
            final Provider<ManualFencePopupPresenterWidget> manualFenceConfirmPopupProvider,
            final Provider<AssignTagsPopupPresenterWidget> assignTagsPopupProvider,
            final Provider<ReportPresenterWidget> reportWindowProvider,
            final Provider<ConfigureLocalStoragePopupPresenterWidget> configureLocalStoragePopupProvider) {
        return new MainTabModelProvider<VDS, HostListModel>(ginjector, HostListModel.class) {
            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(HostListModel source,
                    UICommand lastExecutedCommand, Model windowModel) {
                if (lastExecutedCommand == getModel().getNewCommand()
                        || lastExecutedCommand == getModel().getEditCommand()
                        || lastExecutedCommand == getModel().getEditWithPMemphasisCommand()
                        || lastExecutedCommand == getModel().getApproveCommand()) {
                    return popupProvider.get();
                } else if (lastExecutedCommand == getModel().getAssignTagsCommand()) {
                    return assignTagsPopupProvider.get();
                } else if (lastExecutedCommand == getModel().getConfigureLocalStorageCommand()) {
                    return configureLocalStoragePopupProvider.get();
                }
                return super.getModelPopup(source, lastExecutedCommand, windowModel);
            }

            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends ConfirmationModel, ?> getConfirmModelPopup(HostListModel source,
                    UICommand lastExecutedCommand) {
                if (lastExecutedCommand == getModel().getRemoveCommand()) {
                    return removeConfirmPopupProvider.get();
                } else if (lastExecutedCommand == getModel().getManualFenceCommand()) {
                    return manualFenceConfirmPopupProvider.get();
                } else {
                    return super.getConfirmModelPopup(source, lastExecutedCommand);
                }
            }

            @Override
            protected ModelBoundPresenterWidget<? extends Model> getModelBoundWidget(UICommand lastExecutedCommand) {
                if (lastExecutedCommand instanceof ReportCommand) {
                    return reportWindowProvider.get();
                } else {
                    return super.getModelBoundWidget(lastExecutedCommand);
                }
            }
        };
    }

    // Form Detail Models

    @Provides
    @Singleton
    public DetailModelProvider<HostListModel, HostGeneralModel> getHostGeneralProvider(ClientGinjector ginjector,
            final Provider<HostInstallPopupPresenterWidget> installPopupProvider) {
        return new DetailTabModelProvider<HostListModel, HostGeneralModel>(ginjector,
                HostListModel.class,
                HostGeneralModel.class) {
            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(HostGeneralModel source,
                    UICommand lastExecutedCommand, Model windowModel) {
                if (lastExecutedCommand == getModel().getInstallCommand()) {
                    return installPopupProvider.get();
                } else {
                    return super.getModelPopup(source, lastExecutedCommand, windowModel);
                }
            }
        };
    }

    @Provides
    @Singleton
    public DetailModelProvider<HostListModel, HostHardwareGeneralModel> getHostHardwareProvider(ClientGinjector ginjector) {
        return new DetailTabModelProvider<HostListModel, HostHardwareGeneralModel>(ginjector,
                HostListModel.class,
                HostHardwareGeneralModel.class);
    }

    // Searchable Detail Models

    @Provides
    @Singleton
    public SearchableDetailModelProvider<Map<String, String>, HostListModel, HostHooksListModel> getHostHooksListProvider(ClientGinjector ginjector) {
        return new SearchableDetailTabModelProvider<Map<String, String>, HostListModel, HostHooksListModel>(ginjector,
                HostListModel.class,
                HostHooksListModel.class);
    }

    @Provides
    @Singleton
    public SearchableDetailModelProvider<HostInterfaceLineModel, HostListModel, HostInterfaceListModel> getHostInterfaceListProvider(ClientGinjector ginjector,
            final Provider<DetachConfirmationPopupPresenterWidget> detachConfirmPopupProvider,
            final Provider<HostManagementConfirmationPopupPresenterWidget> hostManagementConfirmationdetachConfirmPopupProvider,
            final Provider<HostInterfacePopupPresenterWidget> hostInterfacePopupProvider,
            final Provider<SetupNetworksInterfacePopupPresenterWidget> setupNetworksInterfacePopupProvider,
            final Provider<HostManagementPopupPresenterWidget> hostManagementPopupProvider,
            final Provider<SetupNetworksManagementPopupPresenterWidget> setupNetworksManagementPopupProvider,
            final Provider<HostBondPopupPresenterWidget> hostBondPopupProvider,
            final Provider<SetupNetworksBondPopupPresenterWidget> setupNetworksBondPopupProvider,
            final Provider<HostSetupNetworksPopupPresenterWidget> hostSetupNetworksPopupProvider) {
        return new SearchableDetailTabModelProvider<HostInterfaceLineModel, HostListModel, HostInterfaceListModel>(ginjector,
                HostListModel.class,
                HostInterfaceListModel.class) {
           @Override
                public AbstractModelBoundPopupPresenterWidget<? extends ConfirmationModel, ?> getConfirmModelPopup(HostInterfaceListModel source,
                        UICommand lastExecutedCommand) {
                   if (StringHelper.stringsEqual(lastExecutedCommand.getName(), "OnEditManagementNetworkConfirmation")) { //$NON-NLS-1$
                       return hostManagementConfirmationdetachConfirmPopupProvider.get();
                   }
                   return super.getConfirmModelPopup(source, lastExecutedCommand);
                }
            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(HostInterfaceListModel source,
                    UICommand lastExecutedCommand, Model windowModel) {
                if (source.getWindow() instanceof HostSetupNetworksModel){
                    // Resolve by dialog model
                    if (windowModel instanceof HostBondInterfaceModel) {
                        return setupNetworksBondPopupProvider.get();
                    } else if (windowModel instanceof HostInterfaceModel) {
                        HostInterfaceModel hostInterfaceModel = (HostInterfaceModel) windowModel;

                        if (hostInterfaceModel.isSetupNetworkMode()){
                            return setupNetworksInterfacePopupProvider.get();
                        }else{
                            return hostInterfacePopupProvider.get();
                        }
                    } else if (windowModel instanceof HostManagementNetworkModel) {
                        HostManagementNetworkModel hostManagementNetworkModel  = (HostManagementNetworkModel) windowModel;

                        if (hostManagementNetworkModel.isSetupNetworkMode()){
                            return setupNetworksManagementPopupProvider.get();
                        }else{
                            return hostManagementPopupProvider.get();
                        }
                    }
                }

                // Resolve by last executed command
                if (lastExecutedCommand == getModel().getEditCommand()) {
                    return hostInterfacePopupProvider.get();
                } else if (lastExecutedCommand == getModel().getEditManagementNetworkCommand()) {
                    return hostManagementPopupProvider.get();
                } else if (lastExecutedCommand == getModel().getBondCommand()) {
                    return hostBondPopupProvider.get();
                } else if (lastExecutedCommand == getModel().getSetupNetworksCommand()) {
                    return hostSetupNetworksPopupProvider.get();
                } else if (lastExecutedCommand == getModel().getDetachCommand()) {
                    return detachConfirmPopupProvider.get();
                } else {
                    return super.getModelPopup(source, lastExecutedCommand, windowModel);
                }
            }

            @Override
            protected void updateData() {
                // Pass empty data to data provider, since Host NIC table is used as header-only table
                updateDataProvider(new ArrayList<HostInterfaceLineModel>());
            }
        };
    };

    @Provides
    @Singleton
    public SearchableDetailModelProvider<VM, HostListModel, HostVmListModel> getHostVmListProvider(ClientGinjector ginjector,
            final Provider<VmMigratePopupPresenterWidget> migratePopupProvider) {
        return new SearchableDetailTabModelProvider<VM, HostListModel, HostVmListModel>(ginjector,
                HostListModel.class,
                HostVmListModel.class) {

            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(HostVmListModel source,
                    UICommand lastExecutedCommand, Model windowModel) {
                if (lastExecutedCommand == getModel().getMigrateCommand()) {
                    return migratePopupProvider.get();
                }
                return super.getModelPopup(source, lastExecutedCommand, windowModel);
            }
        };
    }

    @Provides
    @Singleton
    public SearchableDetailModelProvider<permissions, HostListModel, PermissionListModel> getPermissionListProvider(ClientGinjector ginjector,
            final Provider<PermissionsPopupPresenterWidget> popupProvider,
            final Provider<RemoveConfirmationPopupPresenterWidget> removeConfirmPopupProvider) {
        return new SearchableDetailTabModelProvider<permissions, HostListModel, PermissionListModel>(ginjector,
                HostListModel.class,
                PermissionListModel.class) {

            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(PermissionListModel source,
                    UICommand lastExecutedCommand, Model windowModel) {
                if (lastExecutedCommand == getModel().getAddCommand()) {
                    return popupProvider.get();
                } else {
                    return super.getModelPopup(source, lastExecutedCommand, windowModel);
                }
            }

            @Override
            public AbstractModelBoundPopupPresenterWidget<? extends ConfirmationModel, ?> getConfirmModelPopup(PermissionListModel source,
                    UICommand lastExecutedCommand) {
                if (lastExecutedCommand == getModel().getRemoveCommand()) {
                    return removeConfirmPopupProvider.get();
                } else {
                    return super.getConfirmModelPopup(source, lastExecutedCommand);
                }
            }
        };
    }

    @Provides
    @Singleton
    public SearchableDetailModelProvider<AuditLog, HostListModel, HostEventListModel> getHostEventListProvider(ClientGinjector ginjector) {
        return new SearchableDetailTabModelProvider<AuditLog, HostListModel, HostEventListModel>(ginjector,
                HostListModel.class,
                HostEventListModel.class);
    }

    @Override
    protected void configure() {
    }

}
