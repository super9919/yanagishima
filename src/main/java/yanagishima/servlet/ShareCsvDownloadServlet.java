package yanagishima.servlet;

import me.geso.tinyorm.TinyORM;
import yanagishima.row.Publish;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static yanagishima.util.DownloadUtil.downloadCsv;
import static yanagishima.util.HttpRequestUtil.getOrDefaultParameter;

@Singleton
public class ShareCsvDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TinyORM db;

    @Inject
    public ShareCsvDownloadServlet(TinyORM db) {
        this.db = db;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> publishIdOptional = Optional.ofNullable(request.getParameter("publish_id"));
        if (publishIdOptional.isEmpty()) {
            return;
        }

        String publishId = publishIdOptional.get();
        db.single(Publish.class).where("publish_id=?", publishId).execute().ifPresent(publish -> {
            String fileName = publishId + ".csv";
            String encode = getOrDefaultParameter(request, "encode", "UTF-8");
            boolean showHeader = getOrDefaultParameter(request, "header", true);
            boolean showBOM = getOrDefaultParameter(request, "bom", true);
            downloadCsv(response, fileName, publish.getDatasource(), publish.getQueryId(), encode, showHeader, showBOM);
        });
    }
}
