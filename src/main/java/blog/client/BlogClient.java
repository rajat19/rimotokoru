package blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {

    private static BlogId createBlog(final BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            final BlogId createResponse = stub.createBlog(
                    Blog.newBuilder()
                            .setAuthor("Rajat")
                            .setTitle("New blog")
                            .setContent("This is a new blog")
                            .build()
            );
            System.out.println("Blog created:: " + createResponse.getId());
            return createResponse;
        } catch (final StatusRuntimeException e) {
            System.out.println("Couldn't create the blog");
            e.printStackTrace();
            return null;
        }
    }

    private static void readBlog(final BlogServiceGrpc.BlogServiceBlockingStub stub, final BlogId blogId) {
        try {
            final Blog readResponse = stub.readBlog(blogId);
            System.out.println("Blog read:: \n" + readResponse);
        } catch (final StatusRuntimeException e) {
            System.out.println("Couldn't read the blog");
            e.printStackTrace();
        }
    }

    private static void updateBlog(final BlogServiceGrpc.BlogServiceBlockingStub stub, final BlogId blogId) {
        try {
            final Blog newBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setTitle("New Title")
                    .setAuthor("Abhinav")
                    .setContent("Changed content for blog")
                    .build();

            stub.updateBlog(newBlog);
            System.out.println("Blog updated:: " + newBlog);
        } catch (final StatusRuntimeException e) {
            System.out.println("Couldn't update the blog");
            e.printStackTrace();
        }
    }

    private static void run(final ManagedChannel channel) {
        final BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

        final BlogId blogId = createBlog(stub);
        if (blogId == null) {
            return;
        }

        readBlog(stub, blogId);
        updateBlog(stub, blogId);
    }

    public static void main(final String[] args) {
        final ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        run(channel);

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
