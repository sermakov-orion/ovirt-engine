package org.ovirt.engine.ui.common.uicommon;

import org.ovirt.engine.core.common.queries.ConfigurationValues;
import org.ovirt.engine.core.common.queries.SignStringParameters;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.vms.INoVnc;

import com.google.gwt.user.client.Window.Location;

public class NoVncImpl extends AbstractVnc implements INoVnc {

    private final WebsocketProxyConfig config;

    private String getClientUrl() {
        return Location.getProtocol() + "//" + Location.getHost() + //$NON-NLS-1$
                "/ovirt-engine-novnc-main.html?host=" + config.getProxyHost() + //$NON-NLS-1$
                "&port=" + config.getProxyPort(); //$NON-NLS-1$
    }

    public NoVncImpl() {
        this.config = new WebsocketProxyConfig(
                (String) AsyncDataProvider.getConfigValuePreConverted(ConfigurationValues.WebSocketProxy), getVncHost());
    }

    @Override
    public void invokeClient() {
        AsyncQuery signCallback = new AsyncQuery();
        signCallback.setModel(this);
        signCallback.asyncCallback = new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model, Object returnValue) {
                VdcQueryReturnValue queryRetVal = (VdcQueryReturnValue) returnValue;
                String signature = (String) queryRetVal.getReturnValue();

                WebClientConsoleInvoker invoker = new WebClientConsoleInvoker(signature,
                        getTicket(),
                        getClientUrl());
                invoker.invokeClientNative();
            }
        };

        Frontend.RunQuery(VdcQueryType.SignString,
                new SignStringParameters(WebClientConsoleInvoker.createConnectionString(getVncHost(), getVncPort(), false)),
                signCallback);
    }

}
