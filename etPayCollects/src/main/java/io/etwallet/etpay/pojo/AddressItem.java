package io.etwallet.etpay.pojo;



import java.util.Date;

/**
 * 用户地址
 * <p>
 * Created by liqinqin on 2017/8/2.
 */
public class AddressItem {
    protected static final int LIMIT_SIZE = 40;
    private int childNumber;
    private String address;
    private long userId;
    private Date created;

//    public AddressItem() {
//    }
//
//    public AddressItem(AddressAllocated addressAllocated) {
//        childNumber = addressAllocated.getHdAddress().getIndex();
//        address = addressAllocated.getHdAddress().getAddress();
//        userId = addressAllocated.getUserId();
//        created = new Date(addressAllocated.getCreated());
//    }
//
    public int getChildNumber() {
        return childNumber;
    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public long getUserId() {
//        return userId;
//    }
//
//    public Date getCreated() {
//        return created;
//    }
//
//    public static void addList(Dao dao, Currency currency, Collection<AddressAllocated> addressAllocateds) {
//        if (addressAllocateds.isEmpty()) {
//            return;
//        }
//        AddressMapper addressMapper = dao.getMapper(AddressMapper.class);
//        addressMapper.addList(currency, addressAllocateds.stream().map(AddressItem::new).collect(Collectors.toList()));
//    }
//
//    public static void addList(Currency currency, Collection<AddressAllocated> addressAllocateds) {
//        Dao dao = new Dao().open(false); // 加载 jdbc
//        try {
//            addList(dao, currency, addressAllocateds);
//            dao.commit();
//        } finally {
//            dao.close();
//        }
//    }
//
//    public static Optional<Long> getUserId(Currency currency, String address) {
//        Dao dao = new Dao().open(true); // 加载 jdbc
//        try {
//            AddressMapper addressMapper = dao.getMapper(AddressMapper.class);
//            return Optional.ofNullable(addressMapper.getUserId(currency, address));
//        } finally {
//            dao.close();
//        }
//    }
//
//    
//    public static int getAddressNum(Currency currency, long userId) {
//        Dao dao = new Dao().open(true); // 加载 jdbc
//        try {
//            AddressMapper addressMapper = dao.getMapper(AddressMapper.class);
//            return addressMapper.getTotal(currency, userId);
//        } finally {
//            dao.close();
//        }
//    }
//
//    public static Map<String, AddressItem> findByAddresses(Currency currency, List<String> addresses) {
//        Map<String, AddressItem> addressItemMap = new HashMap<>();
//        if (addresses.isEmpty()) {
//            return addressItemMap;
//        }
//        Dao dao = new Dao().open(true); // 加载 jdbc
//        try {
//            AddressMapper addressMapper = dao.getMapper(AddressMapper.class);
//            List<AddressItem> addressItems = addressMapper.findByAddresses(currency, addresses);
//            if (addressItems != null) {
//                addressItems.forEach(addressItem -> addressItemMap.put(addressItem.address, addressItem));
//            }
//            return addressItemMap;
//        } finally {
//            dao.close();
//        }
//    }
//
//    public static QueryAddressResult query(QueryAddress queryAddress) {
//        QueryAddressResult.Builder resultBuilder = QueryAddressResult.newBuilder()
//                .setCurrency(queryAddress.getCurrency());
//        Dao dao = new Dao().open(true); // 加载 jdbc
//        try {
//            AddressMapper addressMapper = dao.getMapper(AddressMapper.class);
//            int total = addressMapper.getTotal(queryAddress.getCurrency(), queryAddress.getUserId());
//            resultBuilder.setTotal(total);
//            if (total > 0) {
//                int offset = Math.max(0, queryAddress.getOffset()); // 保证偏移 >= 0
//                int limit = Math.min(total - offset, queryAddress.getLimit()); // 计算limit
//                limit = Math.min(limit, LIMIT_SIZE); // 上限
//                if (limit > 0) {
//                    List<AddressItem> addressItems = addressMapper.query(queryAddress.getCurrency(), queryAddress.getUserId(), offset, limit);
//                    if (addressItems != null) {
//                        for (AddressItem addressItem : addressItems) {
//                            resultBuilder.addAddress(AddressMeta.newBuilder()
//                                    .setAddress(addressItem.address)
//                                    .setCreated(addressItem.created.getTime())
//                                    .build());
//                        }
//                    }
//                }
//            }
//        } finally {
//            dao.close();
//        }
//        return resultBuilder.build();
//    }
}
