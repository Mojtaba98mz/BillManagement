package org.example.billmanagement.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.ExpenseDto;
import org.example.billmanagement.controller.dto.TransactionDto;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.BillCalculationService;
import org.example.billmanagement.util.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BillCalculationServiceImpl implements BillCalculationService {

    private MemberRepository memberRepository;

    private GroupRepository groupRepository;

    private SecurityUtils securityUtils;

    @Override
    public List<TransactionDto> calculate(Long groupId) {

        // check user has right access
        groupRepository.findByGroupIdAndUsername(groupId, securityUtils.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("IllegalAccess"));
        // Fetch member payment data
        Map<Long, ExpenseDto> memberPayments = memberRepository.findTotalAmountPaidByEachMemberInGroup(groupId).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new ExpenseDto((String) row[1], (Double) row[2])
                ));

        // Calculate total expenses and equal share
        double totalExpenses = memberPayments.values().stream()
                .mapToDouble(ExpenseDto::getAmount)
                .sum();
        double equalShare = totalExpenses / memberPayments.size();

        // Calculate balances
        Map<Long, Double> balances = memberPayments.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAmount() - equalShare
                ));

        // Separate creditors and debtors
        Queue<Map.Entry<Long, Double>> creditors = createQueue(balances, true);
        Queue<Map.Entry<Long, Double>> debtors = createQueue(balances, false);

        // Settle payments
        return settlePayments(creditors, debtors, memberPayments);
    }

    private Queue<Map.Entry<Long, Double>> createQueue(Map<Long, Double> balances, boolean isCreditor) {
        return balances.entrySet().stream()
                .filter(entry -> isCreditor ? entry.getValue() > 0 : entry.getValue() < 0)
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(
                        (a, b) -> Double.compare(isCreditor ? b.getValue() : a.getValue(),
                                isCreditor ? a.getValue() : b.getValue())
                )));
    }

    private List<TransactionDto> settlePayments(
            Queue<Map.Entry<Long, Double>> creditors,
            Queue<Map.Entry<Long, Double>> debtors,
            Map<Long, ExpenseDto> memberPayments
    ) {
        List<TransactionDto> transactions = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            var creditor = creditors.poll();
            var debtor = debtors.poll();

            double settlement = Math.min(creditor.getValue(), -debtor.getValue());
            transactions.add(new TransactionDto(
                    memberPayments.get(debtor.getKey()).getName(),
                    memberPayments.get(creditor.getKey()).getName(),
                    settlement
            ));

            updateQueue(creditors, creditor, creditor.getValue() - settlement);
            updateQueue(debtors, debtor, debtor.getValue() + settlement);
        }

        return transactions;
    }

    private void updateQueue(Queue<Map.Entry<Long, Double>> queue, Map.Entry<Long, Double> entry, double newBalance) {
        if (newBalance != 0) {
            queue.add(Map.entry(entry.getKey(), newBalance));
        }
    }
}
