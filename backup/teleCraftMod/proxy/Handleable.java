package teleCraftMod.proxy;

public interface Handleable {
	public void handlePreInit();
	public void handleInit();
	public void handlePostInit();
	public void handleLoad();
	public int addArmor(String name);
}
