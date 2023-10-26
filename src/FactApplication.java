import client.FactClient;

import javax.sound.midi.Soundbank;

public class FactApplication {
    public static void main(String[] args) throws InterruptedException {
        FactClient client = new FactClient("Zhenya");
        System.out.println(client.postFact("Луна - спутник земли"));
        client.postFact("ssseff");
        client.postFact("rtyrtu");
        client.postFact("xcbdghtyhk");
        client.postFact("qwetryikjh");
        Thread.sleep(5000);
        client.getFactById(0);
    }
}
