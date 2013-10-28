/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author simplyianm
 */
public class CurrencyInventory implements CurrencyHolder {

    private final Inventory i;

    public CurrencyInventory(Player p) {
        this.i = p.getInventory();
    }

    @Override
    public int balance() {
        int sum = 0;
        for (ItemStack s : stacks()) {
            // Check for null itemstack
            if (s == null) {
                continue;
            }

            sum += Itemconomy.i().getValue(s.getType()) * s.getAmount();
        }
        return sum;
    }

    /**
     * Gets all pertinent ItemStacks.
     *
     * @return
     */
    public List<ItemStack> stacks() {
        List<ItemStack> r = new ArrayList<ItemStack>();
        for (ItemStack s : i.getContents()) {
            // Check for null itemstack
            if (s == null) {
                continue;
            }

            for (Entry<Material, Integer> e : Itemconomy.i().currencySet()) {
                if (s.getType() == e.getKey()) {
                    r.add(s);
                }
            }
        }
        return r;
    }

    /**
     * {@inheritDoc} Uses the largest denominations of currency possible.
     *
     * @param amt
     */
    @Override
    public boolean add(int amt) {
        if (amt < 0) {
            throw new IllegalArgumentException("Amount transferred cannot be negative!");
        }

        // Sort using a tree map
        SortedMap<Integer, Material> s = new TreeMap<Integer, Material>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1; // Sorted backwards
            }

        });

        // Place elements to sort
        for (Entry<Material, Integer> e : Itemconomy.i().currencySet()) {
            s.put(e.getValue(), e.getKey());
        }

        for (Entry<Integer, Material> e : s.entrySet()) {
            int jay = amt / e.getKey();

            ItemStack a = new ItemStack(e.getValue(), jay);
            Map<Integer, ItemStack> res = i.addItem(a);

            if (!res.isEmpty()) {
                int z = 0;
                for (ItemStack i : res.values()) {
                    z += i.getAmount();
                }
                i.remove(new ItemStack(e.getValue(), jay - z)); // jay - z
                return false;
            }
        }

        return true;
    }

    @Override
    public int remove(int amt) {
        if (amt < 0) {
            throw new IllegalArgumentException("Amount transferred cannot be negative!");
        }

        Map<Material, Integer> ret = new EnumMap<Material, Integer>(Material.class);

        // Sort using a tree map
        SortedMap<Integer, Material> s = new TreeMap<Integer, Material>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1; // Sorted backwards
            }

        });

        // Place elements to sort
        for (Entry<Material, Integer> e : Itemconomy.i().currencySet()) {
            s.put(e.getValue(), e.getKey());
        }

        // Iterate backwards -- get rid of the blocks first
        for (Entry<Integer, Material> e : s.entrySet()) {
            int removed = amt / e.getKey();
            int surplus = removed;

            ItemStack[] contents = i.getContents();
            for (int a = 0; a < contents.length; a++) {
                ItemStack t = contents[a];

                if (surplus == 0) {
                    break;
                }

                // Check for null itemstack
                if (t == null) {
                    continue;
                }

                if (t.getType() == e.getValue()) {
                    // Find amt to remove
                    int rem = t.getAmount();
                    if (rem > surplus) {
                        rem = surplus;
                    }

                    // Remove the item
                    Integer curr = ret.get(e.getValue());
                    if (curr == null) {
                        curr = 0;
                    }
                    ret.put(e.getValue(), curr + rem);

                    surplus -= rem;
                }
            }

            // Recalculate amt of items removed
            removed -= surplus;

            amt -= removed * e.getKey();
        }

        if (amt != 0) {
            return amt;
        }

        for (Entry<Material, Integer> e : ret.entrySet()) {
            i.removeItem(new ItemStack(e.getKey(), e.getValue()));
        }

        return amt;
    }

}
