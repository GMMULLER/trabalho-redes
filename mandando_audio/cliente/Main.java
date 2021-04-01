public class Main{
    public static void main(String[] args){
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 6666);
        // String response = client.sendMessage("hello server");
        client.sendMessage("hello server");
        System.out.println("Mensagem enviada");
    }
}