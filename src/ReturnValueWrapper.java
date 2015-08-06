import java.util.TreeSet;

/**
 * This class wraps the return values from AddInterest and RemoveInterest methods.
 * interestList - categories / interests actually added or deleted.
 * returnValue - actual return value as desired by the functions
 * @author Ketan Joshi
 */
public class ReturnValueWrapper
{
	public TreeSet<Integer> interestList;
	public int returnValue;
	
	public ReturnValueWrapper()
	{
		interestList = new TreeSet<Integer>();
		returnValue = 0;
	}
}
