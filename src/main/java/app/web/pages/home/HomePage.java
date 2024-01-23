package app.web.pages.home;

import app.model.Todo;
import app.services.AJAXDownload;
import app.services.ExcelGeneratorService;
import app.services.MongoDBService;
import app.services.PdfGeneratorService;
import app.web.pages.BasePage;
import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.wicketstuff.annotation.mount.MountPath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;


//Wicket is providing all the communication
// with the client without the need of JavaScript
@WicketHomePage
@MountPath(value = "home", alt = {"home2"})
@Slf4j
public class HomePage extends BasePage {

    //from  org.apache.wicket.spring.injection.annot.SpringBean;
    //The @SpringBean annotation in the context of Apache Wicket is used for integrating Spring beans
    // with Wicket components
    //In Apache Wicket, components often have dependencies on other objects,
    // such as services or data access objects. By using @SpringBean,
    // you can inject Spring-managed beans into your Wicket components,
    // which can make it easier to manage dependencies and integrate Wicket with Spring
    @SpringBean
    private MongoDBService mongoDBService;

    @SpringBean
    private ExcelGeneratorService excelGeneratorService;

    @SpringBean
    private PdfGeneratorService pdfGeneratorService;

    FeedbackPanel feedbackPanel;
    List<Todo> toDos;


    public HomePage() {

        //In html we use standard html, we enhance existing syntax using wicket id
        //which point to components on java side of implementation
        Label label = new Label("label", "Apache Wicket - To do application ("
                + mongoDBService.getRepo().count() + ")");

        //Add method that comes from the base page
        add(label);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);

        //In Apache Wicket, WebMarkupContainer is a fundamental component that represents a container for
        // other components in a Wicket application
        //You can update the content of components inside the WebMarkupContainer
        // dynamically based on user actions or other events
        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");

        //if not set it to true, wicket will not be able to update the content of this component
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        Form<Void> form = new Form<>("form");
        sectionForm.add(form);

        WebMarkupContainer formNew = new WebMarkupContainer("formNew");

        AjaxLink<Void> btnAdd = new AjaxLink<>("addItemLink") {
            //The AjaxRequestTarget is an object that allows  to specify which
            // components on the page should be updated or  manipulated during an Ajax request.
            @Override
            public void onClick(AjaxRequestTarget target) {
                formNew.setVisible(!formNew.isVisible());

                target.add(formNew);
            }
        };

        //We are overriding method getFileName()  for getting a file name
        //then we have the implementation of getResourceStream() that's actually
        //streaming the content of the workbook back to the browser
        // and everything is  covered in this download
        //object that is added to the form and to the screen.
        AJAXDownload downloadExcel = new AJAXDownload() {
            @Override
            protected String getFileName() {
                return "excel-todos.xlsx";
            }

            @Override
            protected IResourceStream getResourceStream() {
                return new AbstractResourceStreamWriter() {
                    @Override
                    public void write(OutputStream outputStream) throws IOException {
                        Workbook wb = excelGeneratorService.createExcelFile(toDos);
                        wb.write(outputStream);
                    }
                };
            }
        };
        form.add(downloadExcel);


        //We implement the on click method so
        // that downloadExcel object  is called that has been created above,then
        // provide  the target.
        AjaxLink<Void> downloadExcelBtn = new AjaxLink<>("downloadExcelBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // The  downloadExcel object has a method named initiate, and it takes
                // the target as an argument.
                // This method(initiate) contains the logic for initiating
                // the download  in response to the Ajax request.
                downloadExcel.initiate(target);
            }
        };


        AjaxLink<Void> btnRemove = new AjaxLink<>("remove") {
            //In summary, the target parameter in Apache Wicket is used to specify which components
            // on the page should be updated or manipulated during an
            // Ajax request triggered by the user's interaction with the AjaxLink or similar components.
            // In other words,
            //The target parameter is an AjaxRequestTarget that allows  to specify what should
            // be updated or manipulated on the client side as a result of the Ajax request.
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Todo> toDosToRemove = toDos.stream()
                        .filter(Todo::isSelected)
                        .collect(Collectors.toList());
                mongoDBService.removeItems(toDosToRemove);

                toDos.clear();
                toDos.addAll(mongoDBService.getAllItems());

                if (toDos.size() == 0) {
                    downloadExcelBtn.setVisible(false);
                }


                showInfo(target, "Selected items (" + toDosToRemove.size() + ") removed ...");
                target.add(sectionForm);
            }
        };

        //The btn remove button does also a full form submit with this behavior
        btnRemove.add(new AjaxFormSubmitBehavior(form, "click") {
        });

        AJAXDownload downloadPdf = new AJAXDownload() {
            @Override
            protected String getFileName() {
                return "todos.pdf";
            }

            @Override
            protected IResourceStream getResourceStream() {
                return new AbstractResourceStreamWriter() {
                    @Override
                    public void write(OutputStream outputStream) throws IOException {
                        ByteArrayOutputStream b = pdfGeneratorService.createdPdf(toDos);
                        outputStream.write(b.toByteArray());
                    }
                };
            }
        };
        form.add(downloadPdf);

        //PDF
        AjaxLink<Void> downloadPdfBtn = new AjaxLink<Void>("downloadPdfBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                downloadPdf.initiate(target);
            }
        };

        //toolbar
        form.add(btnAdd, btnRemove, downloadExcelBtn, downloadPdfBtn);

        formNew.setOutputMarkupPlaceholderTag(true);
        formNew.setVisible(false);
        form.add(formNew);

        Todo todoItem = new Todo();
        form.setDefaultModel(new CompoundPropertyModel<>(todoItem));

        TextField<String> title = new TextField<>("title");
        TextField<String> body = new TextField<>("body");
        formNew.add(title, body);

        AjaxLink<Void> btnSave = new AjaxLink<>("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Todo todo = new Todo();
                todo.setTitle(title.getValue());
                todo.setBody(body.getValue());
                mongoDBService.save(todo);

                todoItem.setTitle("");
                todoItem.setBody("");

                formNew.setVisible(false);
                downloadExcelBtn.setVisible(true);

                //in order to add the new added to do item
                //in the page without reload
                toDos.clear();
                toDos.addAll(mongoDBService.getAllItems());

                showInfo(target, "Todo saved into database");
                target.add(sectionForm);
            }
        };

        btnSave.add(new AjaxFormSubmitBehavior(form, "click") {
        });

        formNew.add(btnSave);

        toDos = mongoDBService.getAllItems();
        ListView<Todo> todosList = new ListView<>("todosList", toDos) {
            @Override
            protected void populateItem(ListItem<Todo> item) {
                //bound property to model
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));
                item.add(new Label("title",
                        new PropertyModel<String>(item.getModel(), "title")));

                item.add(new Label("body",
                        () -> item.getModelObject().getBody()));
            }
        };
        //all items in the list will be reused and not re-fetched
        // when the list view is being refreshed
        todosList.setReuseItems(true);
        form.add(todosList);


        AjaxLink<Void> btnSelectAll = new AjaxLink<>("btnSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                toDos.forEach(todo -> todo.setSelected(true));
                target.add(sectionForm);
            }
        };

        AjaxLink<Void> btnDeselectAll = new AjaxLink<>("btnDeSelectAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                toDos.forEach(todo -> todo.setSelected(false));
                target.add(sectionForm);
            }
        };

        add(btnSelectAll, btnDeselectAll);
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        // info method provided by apache wicket
        info(msg);

        //make wicket update feedback panel
        target.add(feedbackPanel);
    }
}
