package org.ovirt.engine.core.bll.gluster;

import java.util.List;

import org.ovirt.engine.core.bll.context.EngineContext;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterServerService;
import org.ovirt.engine.core.common.queries.gluster.GlusterServiceQueryParameters;

public class GetGlusterServerServicesByClusterIdQuery<P extends GlusterServiceQueryParameters> extends GlusterQueriesCommandBase<P> {

    public GetGlusterServerServicesByClusterIdQuery(P parameters, EngineContext engineContext) {
        super(parameters, engineContext);
    }

    @Override
    protected void executeQueryCommand() {
        List<GlusterServerService> serviceList = null;

        if (getParameters().getServiceType() == null) {
            serviceList = glusterServerServiceDao.getByClusterId(getParameters().getId());
        } else {
            serviceList = glusterServerServiceDao.getByClusterIdAndServiceType(getParameters().getId(), getParameters().getServiceType());
        }

        getQueryReturnValue().setReturnValue(serviceList);
    }
}
