import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LoadBalanceConsumer {
    public static final String QUEUE = "MessageType_LoadBalance";
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建factory
        ConnectionFactory factory = new ConnectionFactory();
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        // 一次只接一个 负载均衡
        channel.basicQos(1);

        // 创建队列,幂等的
        channel.queueDeclare(QUEUE, true, false, true, null);
        // 获取数据
        channel.basicConsume(QUEUE, false, new MyConsumer(channel));

        System.out.println("开始等待MQ中间件消息....");
    }
}

class MyConsumer extends DefaultConsumer {

    private Channel channel;

    public MyConsumer(Channel channel) {
        super(channel);//构造传进来
        this.channel = channel;}

    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("接到中间件的消息了....");
        String message = new String(body, "utf-8");
        int millisecond = Integer.parseInt(message) * 1000;
        try {
            System.out.println("处理" + millisecond + "任务，要" + millisecond + "毫秒！");
            Thread.sleep(millisecond);
            System.out.println(millisecond + "处理完毕");
            System.out.println(envelope.getDeliveryTag());
            this.channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("接收到的消息是：" + message);
    }
}