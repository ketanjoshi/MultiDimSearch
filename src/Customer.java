import java.util.TreeSet;

public class Customer implements Comparable<Customer>
{
	private long id;
	private TreeSet<Integer> interests;
	private double amount;
	private int numOfPurchases;

	public Customer(long cId, int[] categories)
	{
		id = cId;
		interests = new TreeSet<Integer>();
		AddInterests(categories);
		amount = 0;
		numOfPurchases = 0;
	}

	public int getNumOfPurchases()
	{
		return numOfPurchases;
	}

	public void setNumOfPurchases(int numOfPurchases)
	{
		this.numOfPurchases = numOfPurchases;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public TreeSet<Integer> getInterests()
	{
		return interests;
	}

	public void setInterests(TreeSet<Integer> interests)
	{
		this.interests = interests;
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	public ReturnValueWrapper AddInterests(int[] categories)
	{
		int count = 0;
		ReturnValueWrapper rvw = new ReturnValueWrapper();
		for (int i = 0; categories[i] != 0; i++)
		{
			if (interests.add(categories[i]))
			{
				count++;
				rvw.interestList.add(categories[i]);
			}
		}
		rvw.returnValue = count;
		return rvw;
	}

	public ReturnValueWrapper RemoveInterests(int[] categories)
	{
		ReturnValueWrapper rvw = new ReturnValueWrapper();
		for (int i = 0; categories[i] != 0; i++)
		{
			if (interests.remove(categories[i]))
			{
				rvw.interestList.add(categories[i]);
			}
		}

		rvw.returnValue = interests.size();

		return rvw;
	}

	public double AddRevenue(double purchase)
	{
		numOfPurchases++;
		amount += purchase;
		return amount;
	}

	@Override
	public int hashCode()
	{
		return (int) id;
	}

	@Override
	public boolean equals(Object obj)
	{
		Customer c = (Customer) obj;
		return this.id == c.id;
	}

	@Override
	public int compareTo(Customer o)
	{
		if (Long.compare(this.id, o.id) == 0)
		{
			return 0;
		}
		else if (Double.compare(o.amount, this.amount) == 0)
		{
			return (Long.compare(o.id, this.id));
		}
		return Double.compare(o.amount, this.amount);
	}
}
