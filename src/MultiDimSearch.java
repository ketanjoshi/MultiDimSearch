import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Program to perform multidimensional search.
 * 
 * @author Ketan Joshi
 */
public class MultiDimSearch
{
	static int[] categories;
	static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
	private int phase = 0;
	private long startTime, endTime, elapsedTime;

	private TreeMap<Long, Customer> customerMap;
	private TreeMap<Double, Integer> amtMap;
	private TreeMap<Integer, TreeSet<Customer>> categoryMap;
	private HashMap<TreeSet<Integer>, Integer> interestMap;

	public MultiDimSearch()
	{
		customerMap = new TreeMap<Long, Customer>();
		amtMap = new TreeMap<Double, Integer>();
		categoryMap = new TreeMap<Integer, TreeSet<Customer>>();
		interestMap = new HashMap<TreeSet<Integer>, Integer>();
	}

	public static void main(String[] args)
	{
		categories = new int[NUM_CATEGORIES];
		Scanner in;
		if (args.length > 0)
		{
			try
			{
				in = new Scanner(new File(args[0]));
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Input file not found. Please provide the input on standard console.");
				in = new Scanner(System.in);
			}
		}
		else
		{
			in = new Scanner(System.in);
		}
		MultiDimSearch x = new MultiDimSearch();
		x.Timer();
		long rv = x.Driver(in);
		System.out.println(rv);
		x.Timer();
	}

	/**
	 * Read categories from in until a 0 appears. Values are copied into static
	 * array categories. Zero marks end.
	 * 
	 * @param in : Scanner from which inputs are read
	 * @return : Number of categories scanned
	 */
	public static int ReadCategories(Scanner in)
	{
		int cat = in.nextInt();
		int index = 0;
		while (cat != 0)
		{
			categories[index++] = cat;
			cat = in.nextInt();
		}
		categories[index] = 0;
		return index;
	}

	/**
	 * Reads the input from input and calculates a value based on the return
	 * values of the operations performed.
	 * 
	 * @param in - Scanner object which points to input source (file or
	 *            console).
	 * @return - final consolidated return value.
	 */
	public long Driver(Scanner in)
	{
		String s;
		long rv = 0, id;
		int cat;
		double purchase;

		while (in.hasNext())
		{
			s = in.next();
			if (s.charAt(0) == '#')
			{
				s = in.nextLine();
				continue;
			}
			if (s.equals("Insert"))
			{
				id = in.nextLong();
				ReadCategories(in);
				rv += Insert(id, categories);
			}
			else if (s.equals("Find"))
			{
				id = in.nextLong();
				rv += Find(id);
			}
			else if (s.equals("Delete"))
			{
				id = in.nextLong();
				rv += Delete(id);
			}
			else if (s.equals("TopThree"))
			{
				cat = in.nextInt();
				rv += TopThree(cat);
			}
			else if (s.equals("AddInterests"))
			{
				id = in.nextLong();
				ReadCategories(in);
				rv += AddInterests(id, categories);
			}
			else if (s.equals("RemoveInterests"))
			{
				id = in.nextLong();
				ReadCategories(in);
				rv += RemoveInterests(id, categories);
			}
			else if (s.equals("AddRevenue"))
			{
				id = in.nextLong();
				purchase = in.nextDouble();
				rv += AddRevenue(id, purchase);
			}
			else if (s.equals("Range"))
			{
				double low = in.nextDouble();
				double high = in.nextDouble();
				rv += Range(low, high);
			}
			else if (s.equals("SameSame"))
			{
				rv += SameSame();
			}
			else if (s.equals("NumberPurchases"))
			{
				id = in.nextLong();
				rv += NumberPurchases(id);
			}
			else if (s.equals("End"))
			{
				return rv % 997;
			}
			else
			{
				System.out.println("Houston, we have a problem.\nUnexpected line in input: " + s);
				System.exit(0);
			}
		}

		rv = rv % MOD_NUMBER;
		return rv;
	}

	public void Timer()
	{
		if (phase == 0)
		{
			startTime = System.currentTimeMillis();
			phase = 1;
		}
		else
		{
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			Memory();
			phase = 0;
		}
	}

	/**
	 * Method to print the memory usage.
	 */
	public void Memory()
	{
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed / 1000000 + " MB / " + memAvailable / 1000000 + " MB.");
	}

	/**
	 * Method to add a new customer (with amount = 0) who is interested in the
	 * given set of categories.
	 * 
	 * @param id - customer id.
	 * @param categories - categories to be added to this new customer's list of
	 *            interests. End of the array should be indicated by appending 0.
	 * @return - 1 if the operation was successful. -1 if there is already
	 *         another customer with the same id (no changes are made in this case).
	 */
	public int Insert(long id, int[] categories)
	{
		if (customerMap.containsKey(id))
			return -1;

		Customer customer = new Customer(id, categories);
		customerMap.put(id, customer);
		AddToCategoryMap(customer);
		AddToAmountMap(customer.getAmount());
		AddToInterestMap(customer);
		return 1;
	}

	/**
	 * Method to find out the amount spent by customer until now.
	 * 
	 * @param id - customer id.
	 * @return - value of the amount field of the customer. -1 if no such customer exists.
	 */
	public int Find(long id)
	{
		return !customerMap.containsKey(id) ? -1 : (int) customerMap.get(id).getAmount();
	}

	/**
	 * Method to delete customer's records from storage.
	 * 
	 * @param id - customer id.
	 * @return - amount field of the deleted customer. -1 if no such customer exists.
	 */
	public int Delete(long id)
	{
		if (!customerMap.containsKey(id))
			return -1;

		Customer customer = customerMap.get(id);
		customerMap.remove(id);
		RemoveFromAmountMap(customer);
		RemoveFromCategoryMap(customer);
		RemoveFromInterestMap(customer);
		return (int) customer.getAmount();
	}

	/**
	 * Method to find the top three customers (in terms of amount spent) who are
	 * interested in particular category.
	 * 
	 * @param cat - category number for which you want the top three customers.
	 * @return - sum of the amounts of the top three customers, truncated to just dollars.
	 */
	public int TopThree(int cat)
	{
		TreeSet<Customer> customers = categoryMap.get(cat);
		if (customers == null)
			return 0;

		Iterator<Customer> customerIterator = customers.iterator();
		double sum = 0;
		for (int i = 0; i < 3; i++)
		{
			if (!customerIterator.hasNext())
				break;
			Customer c = customerIterator.next();
			sum += c.getAmount();
		}
		return (int) sum;
	}

	/**
	 * Add new interests to the list of a customer's categories.
	 * 
	 * @param id - customer id.
	 * @param categories - categories to be added to the customer's list of
	 *            interests. End of the array should be indicated by appending 0.
	 * @return - number of new categories added to that customer's record. -1 if
	 *         no such customer exists.
	 */
	public int AddInterests(long id, int[] categories)
	{
		if (!customerMap.containsKey(id))
			return -1;

		Customer customer = customerMap.get(id);
		RemoveFromInterestMap(customer);
		ReturnValueWrapper rvw = customer.AddInterests(categories);
		customerMap.put(id, customer);
		AddToCategoryMap(customer, rvw.interestList);
		AddToInterestMap(customer);
		return rvw.returnValue;
	}

	/**
	 * Method to remove some categories from the list of categories of interests
	 * associated with a customer.
	 * 
	 * @param id - customer id.
	 * @param categories - categories to be removed from the customer's list of
	 *            interests. End of the array should be indicated by appending 0.
	 * @return - number of categories left in the customer's record. -1 if no
	 *         such customer exists.
	 */
	public int RemoveInterests(long id, int[] categories)
	{
		if (!customerMap.containsKey(id))
			return -1;

		Customer customer = customerMap.get(id);
		RemoveFromInterestMap(customer);
		ReturnValueWrapper rvw = customer.RemoveInterests(categories);
		customerMap.put(id, customer);
		RemoveFromCategoryMap(customer, rvw.interestList);
		AddToInterestMap(customer);
		return rvw.returnValue;
	}

	/**
	 * Method to add a purchase amount spent by a customer on a product.
	 * 
	 * @param id - customer id.
	 * @param purchase - amount spent for particular purchase.
	 * @return - the net amount spent by the customer after adding this
	 *         purchase, truncated to just dollars. -1 if no such customer exists.
	 */
	public int AddRevenue(long id, double purchase)
	{
		if (!customerMap.containsKey(id))
			return -1;

		Customer customer = customerMap.get(id);
		double prevAmt = customer.getAmount();
		RemoveFromCategoryMap(customer);
		RemoveFromAmountMap(prevAmt);

		int value = (int) customer.AddRevenue(purchase);

		customerMap.put(id, customer);
		AddToCategoryMap(customer);
		AddToAmountMap(customer.getAmount());

		return value;
	}

	/**
	 * Method to find out number of customers whose amount is at least "low" and
	 * at most "high".
	 * 
	 * @param low - lower limit of the amount.
	 * @param high - higher limit of the amount.
	 * @return - number of customers whose revenue lies between "low" and "high" (both inclusive).
	 */
	public int Range(double low, double high)
	{
		int count = 0;
		NavigableMap<Double, Integer> subMap = amtMap.subMap(low, true, high, true);
		for (Map.Entry<Double, Integer> entry : subMap.entrySet())
		{
			count += entry.getValue();
		}
		return count;
	}

	/**
	 * Method to find the customers who have exactly the same set of 5 or more
	 * categories of interest. If two customers have exact same categories of
	 * interests, but are less than 5 in number, they will not be considered as
	 * "same".
	 * 
	 * @return - number of distinct customers who have exactly the same set of 5
	 *         or more interests as another customer.
	 */
	public int SameSame()
	{
		int sameCount = 0;
		for (Map.Entry<TreeSet<Integer>, Integer> entry : interestMap.entrySet())
		{
			sameCount += entry.getKey().size() >= 5 && entry.getValue() > 1 ? entry.getValue() : 0;
		}
		System.out.println("SameSame : " + sameCount);
		return sameCount;
	}

	/**
	 * Method to find out the number of times customer has purchased products.
	 * This in turn returns number of calls to AddRevenue for this customer.
	 * 
	 * @param id - customer id.
	 * @return - numbers of purchases for the customer. -1 if no such customer exists.
	 */
	public int NumberPurchases(long id)
	{
		return !customerMap.containsKey(id) ? -1 : customerMap.get(id).getNumOfPurchases();
	}

	/**
	 * Adds an entry for given customer into the category map (against all of
	 * its own categories).
	 * 
	 * @param customer - Customer object.
	 */
	private void AddToCategoryMap(Customer customer)
	{
		AddToCategoryMap(customer, customer.getInterests());
	}

	/**
	 * Adds an entry for given customer into the category map against each
	 * category in the list.
	 * 
	 * @param customer - Customer object.
	 * @param interestList - List of interests against which customer needs to
	 *            be added to the category map.
	 */
	private void AddToCategoryMap(Customer customer, TreeSet<Integer> interestList)
	{
		for (int interest : interestList)
		{
			TreeSet<Customer> cSet = categoryMap.containsKey(interest) ? 
					categoryMap.get(interest) : new TreeSet<Customer>();
			cSet.add(customer);
			categoryMap.put(interest, cSet);
		}
	}

	/**
	 * Removes give customer entry from category map (against all of its own categories).
	 * 
	 * @param customer - Customer object to be removed.
	 */
	private void RemoveFromCategoryMap(Customer customer)
	{
		RemoveFromCategoryMap(customer, customer.getInterests());
	}

	/**
	 * Removes customer entry from amount map (against each category in the
	 * given list).
	 * 
	 * @param customer - Customer object to be removed.
	 * @param interestList - List of interests against which customer needs to
	 *            be removed from the amount map.
	 */
	private void RemoveFromCategoryMap(Customer customer, TreeSet<Integer> interestList)
	{
		for (int i : interestList)
		{
			TreeSet<Customer> cSet = categoryMap.get(i);
			cSet.remove(customer);
		}
	}

	/**
	 * Decrements customer count by 1 from amount map against customer's own
	 * amount value.
	 * 
	 * @param customer - Customer object.
	 */
	private void RemoveFromAmountMap(Customer customer)
	{
		RemoveFromAmountMap(customer.getAmount());
	}

	/**
	 * Decrements customer count by 1 from amount map against given amount
	 * value.
	 * 
	 * @param amountValue - Value of amount against which we need to decrement.
	 */
	private void RemoveFromAmountMap(double amountValue)
	{
		int val = amtMap.get(amountValue) - 1;
		if (val == 0)
			amtMap.remove(amountValue);
		else
			amtMap.put(amountValue, val);
	}

	/**
	 * Increments customer count by 1 in the amount map against given amount
	 * value.
	 * 
	 * @param amountValue - Value of amount against which we need to increment.
	 */
	private void AddToAmountMap(double amountValue)
	{
		amtMap.put(amountValue, amtMap.containsKey(amountValue) ?
				amtMap.get(amountValue) + 1 : 1);
	}

	/**
	 * Increments customer count by 1 in the interest map against given
	 * customer's interest set.
	 * 
	 * @param customer - Customer object.
	 */
	private void AddToInterestMap(Customer customer)
	{
		TreeSet<Integer> customerInterests = customer.getInterests();
		int numOfCustomers = interestMap.containsKey(customerInterests) ? 
				interestMap.get(customerInterests) + 1 : 1;
		interestMap.put(customerInterests, numOfCustomers);
	}

	/**
	 * Decrements customer count by 1 in the interest map against given
	 * customer's interest set.
	 * 
	 * @param customer - Customer object.
	 */
	private void RemoveFromInterestMap(Customer customer)
	{
		TreeSet<Integer> customerInterests = customer.getInterests();
		int numOfCustomers = interestMap.get(customerInterests) - 1;
		if (numOfCustomers == 0)
			interestMap.remove(customerInterests);
		else
			interestMap.put(customerInterests, numOfCustomers);
	}

}
