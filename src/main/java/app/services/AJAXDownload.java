package app.services;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

public abstract class AJAXDownload extends AbstractAjaxBehavior {

    public AJAXDownload() {

    }

    //This method is called to initiate the download. It builds the download URL, appends an anti-cache parameter
    // to avoid browser caching, and then uses JavaScript to set window.location.href
    // after a short delay (100 milliseconds in this case).
    // This  triggers a download by redirecting the browser to the specified URL.
    public void initiate(AjaxRequestTarget target) {
        String url = getCallbackUrl().toString();
        url = url + (url.contains("?") ? "&" : "?");
        url = url + "antiCache=" + System.currentTimeMillis();
        //Use JavaScript to initiate the download after a short delay
        target.appendJavaScript("setTimeout(\"window.location.href='" + url + "'\", 100);");
    }


    public void onRequest() {
        //This method is called when a request is made to initiate the download.
        // It creates a ResourceStreamRequestHandler with the resource stream obtained from getResourceStream() and
        // sets the content disposition to "attachment" (indicating a downloadable file).
        // The request handler is then scheduled to be executed after the current request cycle.
        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(getResourceStream(), getFileName());
        handler.setContentDisposition(ContentDisposition.ATTACHMENT);
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }

    protected String getFileName() {
        return null;
    }

    protected abstract IResourceStream getResourceStream();
}
