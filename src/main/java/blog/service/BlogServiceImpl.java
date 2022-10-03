package blog.service;

import com.google.protobuf.Empty;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoCollection<Document> mongoCollection;

    public BlogServiceImpl(final MongoClient mongoClient) {
        final MongoDatabase db = mongoClient.getDatabase("blog");
        mongoCollection = db.getCollection("blog");
    }

    @Override
    public void createBlog(final Blog request, final StreamObserver<BlogId> responseObserver) {
        final Document document = new Document("author", request.getAuthor())
                .append("title", request.getTitle())
                .append("content", request.getContent());

        final InsertOneResult result;
        try {
            result = mongoCollection.insertOne(document);
        } catch (final MongoException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .asRuntimeException());
            return;
        }

        if (!result.wasAcknowledged() || result.getInsertedId() == null) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Blog couldn't be created")
                    .asRuntimeException());
            return;
        }

        final String id = result.getInsertedId().asObjectId().getValue().toString();
        responseObserver.onNext(BlogId.newBuilder().setId(id).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(final BlogId request, final StreamObserver<Blog> responseObserver) {
        if (request.getId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The blog Id cannot be empty")
                    .asRuntimeException());
            return;
        }
        final String id = request.getId();
        final Document result = mongoCollection
                .find(eq("_id", new ObjectId(id)))
                .first();

        if (result == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Blog not found")
                    .augmentDescription("BlogId:: "+id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Blog.newBuilder()
                .setAuthor(result.getString("author"))
                .setTitle(result.getString("title"))
                .setContent(result.getString("content"))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(final Blog request, final StreamObserver<Empty> responseObserver) {
        if (request.getId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The blog Id cannot be empty")
                    .asRuntimeException());
            return;
        }
        final String id = request.getId();
        final Document result = mongoCollection
                .findOneAndUpdate(
                        eq("_id", new ObjectId(id)),
                        combine(
                                set("author", request.getAuthor()),
                                set("title", request.getTitle()),
                                set("content", request.getContent())
                        )
                );
        if (result == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Blog not found")
                    .augmentDescription("BlogId:: "+id)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlog(final BlogId request, final StreamObserver<Empty> responseObserver) {
        super.deleteBlog(request, responseObserver);
    }

    @Override
    public void listBlogs(final Empty request, final StreamObserver<Blog> responseObserver) {
        super.listBlogs(request, responseObserver);
    }
}
