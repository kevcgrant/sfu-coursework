#include <linux/kernel.h>
#include <asm/uaccess.h>
#include <linux/slab.h>
#include "array_stats.h"

// Implementation of the array_stats sys-call

asmlinkage long sys_array_stats(struct array_stats *stats, long data[], long size) 
{	
	// Initialize variables
	long element = 0.0;
	int i;
	struct array_stats *local_array = kmalloc(sizeof(struct array_stats), GFP_KERNEL);

	// Array size <= 0, argument invalid
	if (size <= 0) {
		kfree(local_array);
		return -EINVAL;
	}
	
	if(copy_from_user(&element, &data[0], sizeof(long)) != 0) {
		kfree(local_array);
		return -EFAULT;
	}

	local_array->min = element;
	local_array->max = element;
	local_array->sum = element;

	for (i = 1; i < size; i++) {
		
		if(copy_from_user(&element, &data[i], sizeof(long)) != 0) {
			kfree(local_array);
			return -EFAULT;
		}

		// Update min
		if (element < local_array->min) {
			local_array->min = element;
		}

		// Update max
		if (element > local_array->max) {
			local_array->max = element;
		}

		// Update sum
		local_array->sum += element;
	}

	// Write local_array values to stats
	if (copy_to_user(&(stats->min), &(local_array->min), sizeof(long)) != 0) {
		kfree(local_array);
		return -EFAULT;
	}
	if (copy_to_user(&(stats->max), &(local_array->max), sizeof(long)) != 0) {
		kfree(local_array);
		return -EFAULT;
	}
	if (copy_to_user(&(stats->sum), &(local_array->sum), sizeof(long)) != 0) {
		kfree(local_array);
		return -EFAULT;
	}
	
	kfree(local_array);
	return 0;
}
