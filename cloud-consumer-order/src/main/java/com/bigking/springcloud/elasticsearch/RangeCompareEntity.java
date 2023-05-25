package com.bigking.springcloud.elasticsearch;

import lombok.Data;

@Data
public class RangeCompareEntity<T> {

        public T gt;
        public T lt;
        public T gte;
        public T lte;

    public RangeCompareEntity<T> setGt(T gt) {
        this.gt = gt;
        return this;
    }

    public RangeCompareEntity<T> setLt(T lt) {
        this.lt = lt;
        return this;
    }

    public RangeCompareEntity<T> setGte(T gte) {
        this.gte = gte;
        return this;
    }

    public RangeCompareEntity<T> setLte(T lte) {
        this.lte = lte;
        return this;
    }
}
