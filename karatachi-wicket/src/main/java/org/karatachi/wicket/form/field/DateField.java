package org.karatachi.wicket.form.field;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;

public class DateField extends FormComponentPanel<Date> {
    private static final long serialVersionUID = 1L;

    private FormComponent<Integer> year;
    private FormComponent<Integer> month;
    private FormComponent<Integer> day;

    private int startYear;
    private int endYear;

    public DateField(String id) {
        super(id);
        Calendar cal = Calendar.getInstance();
        startYear = cal.get(Calendar.YEAR) - 2;
        endYear = cal.get(Calendar.YEAR) + 8;
        commonInit(-1);
    }

    public DateField(String id, int startYear, int endYear) {
        super(id);
        this.startYear = startYear;
        this.endYear = endYear;
        commonInit(-1);
    }

    public DateField(String id, int field) {
        super(id);
        Calendar cal = Calendar.getInstance();
        startYear = cal.get(Calendar.YEAR) - 2;
        endYear = cal.get(Calendar.YEAR) + 8;
        commonInit(field);
    }

    public DateField(String id, IModel<Date> model) {
        super(id, model);
        Calendar cal = Calendar.getInstance();
        startYear = cal.get(Calendar.YEAR) - 2;
        endYear = cal.get(Calendar.YEAR) + 8;
        commonInit(-1);
    }

    private void commonInit(int field) {
        setRenderBodyOnly(true);

        add(year =
                new DropDownChoice<Integer>("year",
                        new DateModel(Calendar.YEAR), getYears(),
                        new ChoiceRenderer<Integer>() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public Object getDisplayValue(Integer object) {
                                return object.toString() + "年";
                            }
                        }));
        add(month =
                new DropDownChoice<Integer>("month", new DateModel(
                        Calendar.MONTH), getMonths(),
                        new ChoiceRenderer<Integer>() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public Object getDisplayValue(Integer object) {
                                return Integer.toString(object + 1) + "月";
                            }
                        }));
        add(day =
                new DropDownChoice<Integer>("day", new DateModel(
                        Calendar.DAY_OF_MONTH), getDays(),
                        new ChoiceRenderer<Integer>() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public Object getDisplayValue(Integer object) {
                                return object.toString() + "日";
                            }
                        }));

        switch (field) {
        case Calendar.YEAR:
            month.setVisible(false);
            day.setVisible(false);
            break;
        case Calendar.MONTH:
            year.setVisible(false);
            day.setVisible(false);
            break;
        case Calendar.DAY_OF_MONTH:
            year.setVisible(false);
            month.setVisible(false);
            break;
        }
    }

    @Override
    protected void convertInput() {
        if (year.getConvertedInput() == null
                || month.getConvertedInput() == null
                || day.getConvertedInput() == null) {
            setConvertedInput(null);
            return;
        }

        Calendar cal =
                DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        cal.set(Calendar.YEAR, year.getConvertedInput());
        cal.set(Calendar.MONTH, month.getConvertedInput());
        cal.set(Calendar.DAY_OF_MONTH, day.getConvertedInput());
        setConvertedInput(cal.getTime());
    }

    @Override
    public boolean checkRequired() {
        if (isRequired()) {
            return year.getInput() != null && month.getInput() != null
                    && day.getInput() != null;
        }
        return true;
    }

    private class DateModel implements IModel<Integer>,
            IObjectClassAwareModel<Integer> {
        private static final long serialVersionUID = 1L;
        private final int calendarField;

        public DateModel(int calendarField) {
            this.calendarField = calendarField;
        }

        public void detach() {
        }

        public Integer getObject() {
            Date date = DateField.this.getModelObject();
            if (date == null) {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(calendarField);
        }

        public void setObject(Integer object) {
        }

        public Class<Integer> getObjectClass() {
            return Integer.class;
        }
    }

    private List<Integer> getYears() {
        List<Integer> years = new ArrayList<Integer>(10);
        for (int i = startYear; i <= endYear; ++i) {
            years.add(i);
        }
        return years;
    }

    private List<Integer> getMonths() {
        List<Integer> months = new ArrayList<Integer>(12);
        for (int i = 0; i < 12; ++i) {
            months.add(i);
        }
        return months;
    }

    private List<Integer> getDays() {
        List<Integer> days = new ArrayList<Integer>(31);
        for (int i = 1; i <= 31; ++i) {
            days.add(i);
        }
        return days;
    }
}
