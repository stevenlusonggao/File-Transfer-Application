public class updategui implements Runnable {
	int counter;
	gui g;
	public updategui (int counter, gui g) {
		this.counter = counter;
		this.g = g;
	}
	@Override
	public void run() {
		g.receivedn.setText(Integer.toString(counter));
		return;
	}
}
