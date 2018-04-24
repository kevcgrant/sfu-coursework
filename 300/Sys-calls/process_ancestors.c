#include "process_ancestors.h"
#include <linux/kernel.h>	
#include <linux/sched.h>
#include <linux/cred.h>



asmlinkage long sys_process_ancestors(struct process_info info_array[], long size, long *num_filled) 
{
	int iterator = 0;

	if (size <= 0) 
	{
		return -EINVAL;
	}

	struct task_struct *currentTask = current;

	while (currentTask->parent != currentTask)  {
		
		struct process_info info;
		long pid = (long) current->pid;

		info.pid = pid;

		strncpy(info.name, currentTask->comm, 16);

		long state = (long) currentTask->state;
		info.state = state;

		kuid_t uid = currentTask->cred->uid;

		info.uid = (long) uid.val;

		info.nvcsw = currentTask->nvcsw;

		info.nivcsw = currentTask->nivcsw;

		long j = 0;

		if(&current->children == NULL) {
			return 0;
		}

		struct task_struct *child_task;
		struct list_head *children_list;
		list_for_each(children_list, &currentTask->children) {
			child_task = list_entry(children_list, struct task_struct, children);
			j++;
		}

		info.num_children = j;

		long i = 0;

		if(&currentTask->sibling == NULL) {
			return 0;
		}

		struct task_struct *sibling_task;
		struct list_head *sibling_list;
		list_for_each(sibling_list, &currentTask->sibling) {
			sibling_task = list_entry(sibling_list, struct task_struct, sibling);
			i++;
		}

		info.num_siblings = j;

		currentTask = currentTask->parent;

		info_array[iterator] = info;
		iterator++;
	}

	int k;

	for(k = 0; k < iterator; k++) {
		printk("PID: %ld\n",info_array[k].pid);
		printk("Name: %s\n",info_array[k].name);
		printk("State: %ld\n",info_array[k].state);
		printk("UID: %ld\n",info_array[k].uid);
		printk("NVCSW: %ld\n",info_array[k].nvcsw);
		printk("NIVCSW: %ld\n",info_array[k].nivcsw);
		printk("Number of children: %ld\n",info_array[k].num_children);
		printk("Number of siblings: %ld\n",info_array[k].num_siblings);

	}
	
	return 0;

}

