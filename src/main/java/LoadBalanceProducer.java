import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
public class LoadBalanceProducer {

    public static final String QUEUE = "MessageType_LoadBalance";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        // 创建factory
        ConnectionFactory factory = new ConnectionFactory();
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        // 一次只给发一条任务
        channel.basicQos(1);

        // 创建队列,幂等的
        channel.queueDeclare(QUEUE, true, false, true, null);
        // 发送数据
        channel.basicPublish("", QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, "6".getBytes());
        channel.basicPublish("", QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, "1".getBytes());
        channel.basicPublish("", QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, "1".getBytes());
        channel.basicPublish("", QUEUE, null,"1".getBytes());
        channel.basicPublish("", QUEUE, null, "1".getBytes());
        channel.basicPublish("", QUEUE, null, "1".getBytes());

        System.out.println("消息发送完毕...");
        // 释放资源
        channel.close();
        connection.close();

    }
}
