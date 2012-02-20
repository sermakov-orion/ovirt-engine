package org.ovirt.engine.ui.userportal.gin;

import org.ovirt.engine.ui.common.gin.BaseUiCommonModule;
import org.ovirt.engine.ui.uicommonweb.models.userportal.UserPortalLoginModel;
import org.ovirt.engine.ui.userportal.uicommon.UserPortalConfigurator;
import org.ovirt.engine.ui.userportal.uicommon.model.UserPortalListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.UserPortalModelResolver;
import org.ovirt.engine.ui.userportal.uicommon.model.basic.UserPortalBasicListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateDiskListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateGeneralModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.TemplateInterfaceListModelProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.template.UserPortalTemplateListProvider;
import org.ovirt.engine.ui.userportal.uicommon.model.vm.VmGeneralModelProvider;

import com.google.inject.Singleton;

/**
 * GIN module containing UserPortal UiCommon model and integration bindings.
 */
public class UiCommonModule extends BaseUiCommonModule {

    @Override
    protected void configure() {
        bindModels();
        bindIntegration();
    }

    void bindModels() {
        // Basic tab
        bind(UserPortalBasicListProvider.class).asEagerSingleton();

        // Extended tab: Virtual Machine
        bind(UserPortalListProvider.class).asEagerSingleton();
        bind(VmGeneralModelProvider.class).asEagerSingleton();

        // Extended tab: Template
        bind(UserPortalTemplateListProvider.class).asEagerSingleton();
        bind(TemplateGeneralModelProvider.class).asEagerSingleton();
        bind(TemplateInterfaceListModelProvider.class).asEagerSingleton();
        bind(TemplateDiskListModelProvider.class).asEagerSingleton();
    }

    void bindIntegration() {
        bindCommonIntegration();
        bindConfiguratorIntegration(UserPortalConfigurator.class);
        bind(UserPortalModelResolver.class).asEagerSingleton();
        bind(UserPortalLoginModel.class).in(Singleton.class);
    }

}
