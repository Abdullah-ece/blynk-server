import React from 'react';
import { MainLayout } from 'components';
import { Button } from 'antd';
import '../styless.less';
import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { GetRuleGroup, UpdateRuleGroup } from 'data/RulesEngine/actions';

@connect((state) => ({
  ruleEngine: state.RulesEngine.ruleEngine,
}), (dispatch) => ({
  getRuleGroup: bindActionCreators(GetRuleGroup, dispatch),
  updateRuleGroup: bindActionCreators(UpdateRuleGroup, dispatch),
}))
class Index extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      rules: props.ruleEngine
    };

    this.updateRules = this.updateRules.bind(this);
    this.updateRulesText = this.updateRulesText.bind(this);
  }

  componentWillMount() {
    this.props.getRuleGroup();
  }

  updateRules() {
    this.props.updateRuleGroup(this.state.rules);
  }

  updateRulesText(obj) {
    this.setState({ rules: obj.target.value });
  }

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Rules Engine"
                           options={(
                             <div>
                               <Button type="primary" onClick={this.updateRules}>Update Rules</Button>
                             </div>

                           )}
        />
        <MainLayout.Content
          className="layout-content-rules-engine-text-area product-edit-content">
          <textarea className="rules-engine-text-area"
                    value={JSON.stringify(this.state.rules, undefined, 2)}
                    onChange={this.updateRulesText}></textarea>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default Index;
