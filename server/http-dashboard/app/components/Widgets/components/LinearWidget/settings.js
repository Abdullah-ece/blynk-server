import React from 'react';
import {
  SimpleContentEditable
} from 'components';
import Scroll from "react-scroll";
import Source from './source';
import {
  WIDGET_TYPES,
  WIDGETS_PREDEFINED_SOURCE_OPTIONS
} from 'services/Widgets';
import _ from 'lodash';
import {Button} from 'antd';
import {Preview} from './components';
import {reduxForm, Field, getFormValues, change, reset, destroy, initialize} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {fromJS, Map, List} from 'immutable';
import {FORMS} from 'services/Products';
import PropTypes from 'prop-types';
// import Widget from '../Widget';
import WidgetSettings from '../WidgetSettings';

@connect((state, ownProps) => ({
  formValues: fromJS(getFormValues(ownProps.form)(state) || {}),
  dataStreams: fromJS((() => {
    const formValues = getFormValues(FORMS.PRODUCTS_PRODUCT_MANAGE)(state);

    return (formValues && formValues.dataStreams || []);
  })()),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
class LinearWidgetSettings extends React.Component {

  static propTypes = {
    formValues: PropTypes.instanceOf(Map),
    dataStreams: PropTypes.instanceOf(List),

    visible: PropTypes.bool,
    pristine: PropTypes.bool,

    initialValues: PropTypes.object,

    form: PropTypes.string,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    destroyForm: PropTypes.func,
    handleSubmit: PropTypes.func,
    initializeForm: PropTypes.func,

    history: PropTypes.instanceOf(Map),

    deviceId: PropTypes.number,

    loading: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleSourceChange = this.handleSourceChange.bind(this);
    this.handleAddSource = this.handleAddSource.bind(this);
    this.handleSourceCopy = this.handleSourceCopy.bind(this);
    this.handleSourceDelete = this.handleSourceDelete.bind(this);
  }

  state = {
    value: 'Chart Title',
    value2: 'Temperature',
  };

  componentWillUpdate(nextProps) {

    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initializeForm(nextProps.form, nextProps.initialValues);
      this.props.resetForm(nextProps.form, nextProps.initialValues);
    }
  }

  componentDidUpdate(prevProps) {


    if(prevProps && prevProps.formValues && prevProps.formValues.get('sources')) {
      let newId = null;

      const lastIds = prevProps.formValues.get('sources').map((source) => {
        return Number(source.get('id'));
      });

      this.props.formValues.get('sources').forEach((source) => {
        if(lastIds.indexOf(Number(source.get('id'))) === -1) {
          newId = source.get('id');
        }
      });

      if(newId) {

        setTimeout(() => {
          Scroll.scroller.scrollTo(`source-${newId}`, {
            duration: 1000,
            offset: -32,
            smooth: "easeInOutQuint",
            containerId: "widgetSettingsConfigColumn",
          });
        }, 1);
      }
    }
  }

  handleSourceCopy(id) {
    const nextId = _.random(1, 999999999);

    const sourceIndex = this.props.formValues.get('sources').findIndex((source) => Number(source.get('id')) === Number(id));

    const source = this.props.formValues.getIn(['sources', sourceIndex]);

    const sources = this.props.formValues.get('sources').insert(sourceIndex + 1, fromJS({
      ...source.toJS(),
      id: nextId
    }));

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  handleSourceDelete(deleteIndex) {
    const sources = this.props.formValues.get('sources').update(sources => sources.filter((source, index) => Number(index) !== Number(deleteIndex)));

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    this.props.resetForm(this.props.form);
  }

  handleSave() {
    if(typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  handleAddSource() {

    const id = _.random(1, 999999999);

    const sources = this.props.formValues.get('sources').push({
      ...WIDGETS_PREDEFINED_SOURCE_OPTIONS[WIDGET_TYPES.LINEAR],
      id: id,
    });

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
                             onChange={input.onChange}/>
    );
  }

  handleSourceChange(source) {

    const sources = this.props.formValues.get('sources').map((s) => {
      if (s.get('id') === source.id)
        return fromJS(source);
      return s;
    });

    this.props.changeForm(this.props.form, 'sources', sources);
  }

  render() {

    const dataStreams = this.props.dataStreams.filter(dataStream => dataStream.has('label') && dataStream.get('label').length)
      .map((dataStream) => (fromJS({
        key: String(dataStream.get('pin')),
        value: dataStream.get('label'),
        values: dataStream,
      })));

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        isSaveDisabled={this.props.pristine}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                <Button type="dashed" onClick={this.handleAddSource}>Add source</Button>
              </div>

            </div>
            <div className="modal-window-widget-settings-config-column-sources">

              {this.props.formValues.has('sources') && this.props.formValues.get('sources').map((source, key) => (
                  <Source form={this.props.form}
                          dataStreams={dataStreams}
                          index={key}
                          dataStream={this.props.formValues.getIn(['sources', key, 'dataStream'])}
                          source={source} key={key}
                          onChange={this.handleSourceChange}
                          isAbleToDelete={this.props.formValues.get('sources').size > 1}
                          onDelete={this.handleSourceDelete}
                          onCopy={this.handleSourceCopy}
                  />

              ))}

            </div>
          </div>
        )}

        preview={(
          <Preview
            deviceId={this.props.deviceId}
            data={this.props.formValues.toJS()}
            history={this.props.history}
            loading={this.props.loading}/>
        )}

        // preview={(
        //   <div className="widgets">
        //     <Widget isPreviewOnly={true}
        //             style={{height: '200px'}}
        //             fetchRealData={false}
        //             params={{}}
        //             data={this.props.formValues.toJS()} />
        //   </div>
        // )}
      />
    );
  }

}

export default LinearWidgetSettings;
