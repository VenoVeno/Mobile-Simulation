import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;

public class Phone {
    public static class Mobile {
        public long number;
        public String carrier, brand;

        Mobile() {
        }

        Mobile(String brand, String carrier, long number) {
            this.brand = brand;
            this.carrier = carrier;
            this.number = number;
        }
    }

    public static class Mobiles extends Mobile {
        private int mobiles;

        private Mobile[] mobilesList;
        static int userId = 0;

        Mobiles(int mobiles) {
            super();
            this.mobiles = mobiles;
            this.mobilesList = new Mobile[mobiles];
        }

        public void StoreUserData(String brand, String carrier, long number) {
            mobilesList[userId++] = new Mobile(brand, carrier, number);
        }

        public String FindCarrier(long number) {
            for (int index = 0; index < this.mobiles; index++) {
                if (this.mobilesList[index].number == number)
                    return this.mobilesList[index].carrier.toLowerCase();
            }

            return "";
        }

        public String FindBrand(long number) {
            for (int index = 0; index < this.mobiles; index++) {
                if (this.mobilesList[index].number == number)
                    return this.mobilesList[index].brand.toLowerCase();
            }

            return "";
        }

        public ArrayList<Long> getUserList() {
            ArrayList<Long> list = new ArrayList<>();

            for (int index = 0; index < this.mobiles; index++) {
                list.add(this.mobilesList[index].number);
            }

            return list;
        }

        public void DisplayUserData() {
            for (int i = 0; i < mobiles; i++) {
                System.out.println(this.mobilesList[i].brand + " " + this.mobilesList[i].carrier + " "
                        + this.mobilesList[i].number);
            }
        }
    }

    public static class Message {
        long sender, receiver, cost;
        String message, date;

        Message() {
        }

        Message(long sender, long receiver, String message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
            this.date = new Date().toString();
        }
    }

    public static class MessageQueue extends Message {
        public ArrayList<Message> messageQueue = new ArrayList<>();

        MessageQueue() {
            super();
        }

        public void Push(long sender, long receiver, String message) {
            messageQueue.add(new Message(sender, receiver, message));
        }
    }

    public static class Users {
        ArrayList<Long> userList = new ArrayList<Long>();

        Users(Mobiles mobiles) {
            this.userList = mobiles.getUserList();
        }
    }

    public static class HashMapCaseInsensitive extends HashMap<String, List<Object>> {
        @Override
        public List<Object> put(String key, List<Object> value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public List<Object> get(Object key) {
            return super.get(key.toString().toLowerCase());
        }
    }

    public static class Charge {

        public Map<String, List<Object>> smsCharge = new HashMapCaseInsensitive();

        Charge() {
            this.smsCharge.put("Airtel", Arrays.asList("Vodadfone", 10));
            this.smsCharge.put("Vodafone", Arrays.asList("Airtel", 20));
        }
    }

    public static class SMS {
        Charge charge = new Charge();

        public void sendSMS(Mobiles mobiles, MessageQueue queue) {
            Users users = new Users(mobiles);

            Map<Long, Integer> cost = new HashMap<>();

            System.out.println(users.userList);

            for (int index = 0; index < queue.messageQueue.size(); index++) {
                long sender = queue.messageQueue.get(index).sender;
                long receiver = queue.messageQueue.get(index).receiver;

                System.out.println();
                System.out.println("From : " + sender);
                System.out.println("To : " + queue.messageQueue.get(index).receiver);
                System.out.println("Date Time : " + queue.messageQueue.get(index).date);
                System.out.println(queue.messageQueue.get(index).message);
                System.out.println("Message From " + mobiles.FindBrand(sender));

                if (cost.get(sender) == null) {
                    cost.put(sender, 0);
                }

                System.out.println(charge.smsCharge);

                String senderCarrier = mobiles.FindCarrier(sender);
                String receiverCarrier = mobiles.FindCarrier(receiver);

                System.out.println("From " + senderCarrier + " To " + receiverCarrier);

                if (!senderCarrier.equals(receiverCarrier)) {
                    List<Object> carrierCharge = charge.smsCharge.get(senderCarrier);

                    System.out.println("Charge " + carrierCharge);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numOfMobiles = scanner.nextInt();
        Mobiles mobiles = new Mobiles(numOfMobiles);

        for (int i = 0; i < numOfMobiles; i++) {
            mobiles.StoreUserData(scanner.next(), scanner.next(), scanner.nextLong());
        }

        // mobiles.DisplayUserData();

        MessageQueue smsQueue = new MessageQueue();

        for (int i = 0; i < numOfMobiles; i++) {
            smsQueue.Push(scanner.nextLong(), scanner.nextLong(), scanner.next());
        }

        // smsQueue.Display();

        SMS sms = new SMS();

        sms.sendSMS(mobiles, smsQueue);

        scanner.close();
    }
}