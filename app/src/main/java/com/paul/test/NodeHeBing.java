package com.paul.test;


//输入两个递增的链表，单个链表的长度为n，合并这两个链表并使新链表中的节点仍然是递增排序的。
//        如果链表中的节点数不是 k 的倍数，将最后剩下的节点保持原样
//        你不能更改节点中的值，只能更改节点本身。

public class NodeHeBing {
    public FanNode hebing(FanNode node1, FanNode node2) {
        if (node1 == null ) {
            return node2;
        } else if (node2==null){
            return node1;
        }
        FanNode newNode=new FanNode(-1);
        FanNode cur = newNode;

        FanNode curNode1 = node1;
        FanNode curNode2 = node2;
        FanNode next1 ;
        FanNode next2 ;

        while (curNode1!=null&&curNode2!=null){
            if(curNode1.val<curNode2.val){
                next1 = curNode1.next;
                cur.next = curNode1;
                cur =curNode1;
                curNode1 = next1;
                if(next1==null){
                   cur.next = curNode2;
                }
            }else{
                next2 = curNode2.next;
                cur.next = curNode2;
                cur =curNode2;
                curNode2 = next2;
                if(next2==null){
                    cur.next = curNode1;
                }
            }
        }
        return newNode.next;
    }

    public static void main(String[] args) {
        NodeHeBing heBing = new NodeHeBing();
        //构建测试数据：单链表0->1..->9
        FanNode node1 = new FanNode(1);
        FanNode node2 = new FanNode(2);
        FanNode node3 = new FanNode(3);
        FanNode node4 = new FanNode(4);
        FanNode node5 = new FanNode(5);
        FanNode node6 = new FanNode(6);
        FanNode node44 = new FanNode(44);
        FanNode node23 = new FanNode(23);
        FanNode node33 = new FanNode(33);
        node1.next = node3;
        node3.next =node5;
        node5.next =node23;
        node2.next = node4;
        node4.next =node6;
        node6.next =node33;
        node33.next =node44;


        FanNode newList = heBing.hebing(node1, node2);
        FanNode houNode = newList;
        while (houNode != null) {
            System.out.println("转换后：" + houNode.val);
            houNode = houNode.next;
        }
    }
}
