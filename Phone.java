import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Phone {
    // MOBILE CAN HAVE NUMBER, BRAND AND CARRIER_NAME FOR THE NUMBER
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

    // LIST OF ALL MOBILES
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

        public List<Long> getUserList() {
            List<Long> list = new ArrayList<>();

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

    // ACTUAL MESSAGE DATA
    public static class Message {
        long sender, receiver;
        String message, date;

        Message() {
        }

        Message(long sender, long receiver, String message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;

            SimpleDateFormat ft = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
            this.date = ft.format(new Date());
        }
    }

    // QUEUE ALL MESSAGES TO BE SENT
    public static class MessageQueue extends Message {
        public List<Message> messageQueue = new ArrayList<>();

        MessageQueue() {
            super();
        }

        public void Push(long sender, long receiver, String message) {
            // PUSH TO MESSAGE QUEUE ONLY IF SENDER AND RECEIVER NUMBER ARE DIFFERENT
            if (sender != receiver)
                messageQueue.add(new Message(sender, receiver, message));
        }
    }

    // USERS THAT ARE AVAILABLE WHO OWNS MOBILES
    public static class Users {
        List<Long> userList = new ArrayList<>();

        Users(Mobiles mobiles) {
            this.userList = mobiles.getUserList();
        }
    }

    // MAP INSIDE MAP -- OUTER MAP
    public static class CaseInsensitiveMapInsideMap extends HashMap<String, CaseInsensitiveMap> {
        @Override
        public CaseInsensitiveMap put(String key, CaseInsensitiveMap value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public CaseInsensitiveMap get(Object key) {
            return super.get((Object) key.toString().toLowerCase());
        }
    }

    // MAP WITH KEY, VALUE -- INNER MAP
    public static class CaseInsensitiveMap extends HashMap<String, Integer> {
        @Override
        public Integer put(String key, Integer value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public Integer get(Object key) {
            return super.get((Object) key.toString().toLowerCase());
        }
    }

    // CHARGE FOR THE SMS
    public static class Charge {
        public Map<String, CaseInsensitiveMap> smsCharge = new CaseInsensitiveMapInsideMap();
        String[] providers = { "Airtel", "BSNL", "Jio", "Vodafone" };
        Random random = new Random();

        Charge() {
            for (int index = 0; index < providers.length; index++) {
                String senderName = providers[index];

                this.smsCharge.put(senderName, new CaseInsensitiveMap() {
                    {
                        for (int innerIndex = 0; innerIndex < providers.length; innerIndex++) {
                            if (senderName != providers[innerIndex])
                                put(providers[innerIndex], random.nextInt(50));
                        }
                    }
                });
            }
        }
    }

    // ACTUAL SMS TO BE SENT HAPPENS HERE!
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

                // HEADER
                System.out.println("From : " + sender);
                System.out.println("To : " + receiver);
                System.out.println("DateTime : " + queue.messageQueue.get(index).date);

                // ACTUAL MESSAGE
                System.out.println(queue.messageQueue.get(index).message);

                // SIGNATURE (BRAND NAME)
                System.out.println("Message From " + mobiles.FindBrand(sender));

                if (cost.get(sender) == null) {
                    cost.put(sender, 0);
                }

                String senderCarrier = mobiles.FindCarrier(sender);
                String receiverCarrier = mobiles.FindCarrier(receiver);

                if (!senderCarrier.equalsIgnoreCase(receiverCarrier)) {
                    // FROM SENDER TO RECEIVER HOW MUCH IS THE CHARGE
                    Integer price = charge.smsCharge.get(senderCarrier).get(receiverCarrier);

                    System.out.println("From " + senderCarrier + " To " + receiverCarrier + " costs " + price);

                    // cost.compute(sender, (key, value) -> value == null ? price : value + price);
                    cost.put(sender, cost.get(sender) + price);

                } else {
                    System.out.println("Within Same carrier (" + senderCarrier + ") No charge!");
                }
            }

            this.displaySMSCharge(cost);

            // AFTER ALL MESSAGES ARE SENT CLEAR THE QUEUE
            queue.messageQueue.clear();
        }

        public void displaySMSCharge(Map<Long, Integer> cost) {
            System.out.println("\nTotal SMS Charges:\n");

            for (Map.Entry<Long, Integer> entry : cost.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue() + "p");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("No of Mobiles to get Registered : ");
        int numOfMobiles = scanner.nextInt();

        // LIST OF ALL MOBILES
        Mobiles mobiles = new Mobiles(numOfMobiles);

        System.out.println("\nEnter Brand name, Carrier name, Phone Number");
        while (numOfMobiles-- != 0) {
            mobiles.StoreUserData(scanner.next(), scanner.next(), scanner.nextLong());
        }

        // mobiles.DisplayUserData();

        MessageQueue smsQueue = new MessageQueue();

        System.out.print("\nNo of SMS to be Sent : ");
        int smsCount = scanner.nextInt();

        // TO SAVE MESSAGE DATA IN QUEUE
        System.out.println("\nEnter Sender Number, Receiver Number, Actual Message");
        while (smsCount-- != 0) {
            // READ REMAINING MESSAGE TEXT WITH LEADING WHITE SPACE REMOVED
            smsQueue.Push(scanner.nextLong(), scanner.nextLong(), scanner.nextLine().stripLeading());
        }

        // smsQueue.Display();

        SMS sms = new SMS();
        sms.sendSMS(mobiles, smsQueue);

        scanner.close();
    }
}