package com.newbusiness.one4all.dto;

import java.util.ArrayList;
import java.util.List;

import com.newbusiness.one4all.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownlinerHierarchyDTO {
    private String memberId; // Current member ID
    private String fullName; // Member's full name
    private int leftOverChildrenPosition; // Number of positions left in this member's binary tree
    private List<DownlinerHierarchyDTO> children; // Recursive list of downliner hierarchies
    //private List<Member> allChildrenMembers; // All children member details

    // Recursively get all children member details
	/*
	 * public List<Member> getAllChildrenMembers() { List<Member> members = new
	 * ArrayList<>(allChildrenMembers); for (DownlinerHierarchyDTO child : children)
	 * { members.addAll(child.getAllChildrenMembers()); } return members; }
	 */
}
