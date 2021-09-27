import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

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
                    return this.mobilesList[index].carrier;
            }

            return "";
        }

        public String FindBrand(long number) {
            for (int index = 0; index < this.mobiles; index++) {
                if (this.mobilesList[index].number == number)
                    return this.mobilesList[index].brand;
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

    public static class CaseInsensitiveMap extends HashMap<String, Integer> {
        @Override
        public Integer put(String key, Integer value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public Integer get(Object key) {
            return super.get(key.toString().toLowerCase());
        }
    }

    public static class CaseInsensitiveMapInsideMap extends HashMap<String, CaseInsensitiveMap> {
        @Override
        public CaseInsensitiveMap put(String key, CaseInsensitiveMap value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public CaseInsensitiveMap get(Object key) {
            return super.get(key.toString().toLowerCase());
        }
    }

    public static class Charge {
        public Map<String, CaseInsensitiveMap> smsCharge = new CaseInsensitiveMapInsideMap();

        Charge() {
            this.smsCharge.put("Airtel", new CaseInsensitiveMap() {
                {
                    put("Vodafone", 10);
                    put("Jio", 50);
                }
            });

            this.smsCharge.put("Vodafone", new CaseInsensitiveMap() {
                {
                    put("Airtel", 20);
                    put("Jio", 100);
                }
            });
        }
    }

    public static class SMS {
        Charge charge = new Charge();

        public void sendSMS(Mobiles mobiles, MessageQueue queue) {
            Users users = new Users(mobiles);

            Map<Long, Integer> cost = new HashMap<>();

            System.out.println(users.userList);
            System.out.println(charge.smsCharge);

            for (int index = 0; index < queue.messageQueue.size(); index++) {
                long sender = queue.messageQueue.get(index).sender;
                long receiver = queue.messageQueue.get(index).receiver;

                System.out.println();
                System.out.println("From : " + sender);
                System.out.println("To : " + receiver);
                System.out.println("Date Time : " + queue.messageQueue.get(index).date);
                System.out.println(queue.messageQueue.get(index).message);
                System.out.println("Message From " + mobiles.FindBrand(sender));

                if (cost.get(sender) == null) {
                    cost.put(sender, 0);
                }

                String senderCarrier = mobiles.FindCarrier(sender);
                String receiverCarrier = mobiles.FindCarrier(receiver);

                System.out.println("From " + senderCarrier + " To " + receiverCarrier);

                if (!senderCarrier.equalsIgnoreCase(receiverCarrier)) {
                    // FROM SENDER TO RECEIVER HOW MUCH IS THE CHARGE
                    Integer price = charge.smsCharge.get(senderCarrier).get(receiverCarrier);

                    // cost.compute(sender, (key, value) -> value == null ? price : value + price);
                    cost.put(sender, cost.get(sender) + price);

                } else {
                    System.out.println("Within Same carrier No charge!");
                }
            }
        }

        public void displayCharge(Charge charge) {
            for (int index = 0; index < charge.smsCharge.size(); index++) {

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