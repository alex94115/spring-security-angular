module.exports = function(grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    concat: {
      options: {
        separator: '\n'
      },
      dist: {
        src: [
            'src/main/resources/vendor/jquery-2.1.1.js',
            'src/main/resources/vendor/bootstrap-3.2.0.js',
            'src/main/resources/vendor/angular-1.3.8.js',
            'src/main/resources/vendor/angular-resource-1.3.8.js',
            'src/main/resources/vendor/angular-route-1.3.8.js',
            'src/main/resources/vendor/angular-cookies-1.3.8.js',
            'src/main/resources/static/core/core.module.js',
            'src/main/resources/static/core/config.js',
            'src/main/resources/static/core/constants.js',
            'src/main/resources/static/core/security.js',
            'src/main/resources/static/core/root.js',
            'src/main/resources/static/core/special.js',
            'src/main/resources/static/core/authentication.js',
            'src/main/resources/static/core/startup.js',
            'src/main/resources/static/profile/profile.js',
            'src/main/resources/static/components/shared/filters.js',
            'src/main/resources/static/components/google/google.directive.js',
            'src/main/resources/static/components/facebook/facebook.directive.js',
            'src/main/resources/static/components/facebook/facebookSignIn.directive.js'
        ],
        dest: 'target/generated-resources/static/js/angular-bootstrap.js'
      }
    },
    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
      },
      dist: {
        files: {
          'dist/<%= pkg.name %>.min.js': ['<%= concat.dist.dest %>']
        }
      }
    },
    qunit: {
      files: ['test/**/*.html']
    },
    jshint: {
      files: [
             'Gruntfile.js', 
             'src/main/resources/static/**/*.js', 
             'src/test/resources/js/**/*.js'
             ],
      options: {
        // options here to override JSHint defaults
        globals: {
          //jQuery: true,
          //console: true,
          module: true,
          document: true
        }
      }
    },
    watch: {
      files: ['<%= jshint.files %>'],
      tasks: ['jshint', 'qunit']
    },
    jasmine: {
      src: [
            'src/main/resources/static/app.module.js',
            'src/main/resources/static/core/core.module.js',
            'src/main/resources/static/core/config.js',
            'src/main/resources/static/core/constants.js',
            'src/main/resources/static/core/security.js',
            'src/main/resources/static/core/root.js',
            'src/main/resources/static/core/authentication.js',
            'src/main/resources/static/core/startup.js',
            'src/main/resources/static/profile/profile.js',
            'src/main/resources/static/components/shared/filters.js',
            'src/main/resources/static/components/google/google.directive.js',
            'src/main/resources/static/components/facebook/facebook.directive.js',
            'src/main/resources/static/components/facebook/facebookSignIn.directive.js'
      ],
      options: {
        specs: 'src/test/resources/js/*Spec.js',
        helpers: 'src/main/resources/vendor/angular-mocks-1.3.8.js',
        keepRunner: true,
        vendor: [
            'src/main/resources/vendor/jquery-2.1.1.js',
            'src/main/resources/vendor/bootstrap-3.2.0.js',
            'src/main/resources/vendor/angular-1.3.8.js',
            'src/main/resources/vendor/angular-resource-1.3.8.js',
            'src/main/resources/vendor/angular-route-1.3.8.js',
            'src/main/resources/vendor/angular-cookies-1.3.8.js',
            'src/test/resources/js/support/facebook-js-stub.js',
            'src/test/resources/js/support/gapi-js-stub.js'
        ]
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-qunit');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-jasmine');

  grunt.registerTask('test', ['jshint', 'qunit', 'jasmine']);

  grunt.registerTask('default', ['jshint', 'qunit', 'jasmine',  'concat']);

};
